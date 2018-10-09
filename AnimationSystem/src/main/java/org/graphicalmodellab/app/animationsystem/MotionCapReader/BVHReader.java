package org.graphicalmodellab.app.animationsystem.MotionCapReader;
import java.io.*;
import java.util.*;


import org.graphicalmodellab.app.animationsystem.AnimationObject.EndSite;
import org.graphicalmodellab.app.animationsystem.AnimationObject.Joint;
import org.graphicalmodellab.app.animationsystem.AnimationObject.Root;
import org.graphicalmodellab.app.animationsystem.AnimationObject.TheCharacter;

/**
 * Provide a set of tools reading various kind of animation files
 * @author itoumao
 *
 */
public class BVHReader {
	private static int motion_count;
	private static double[][] motion_data;
	private static HashMap<String,Joint> jointMap;
	private static double frameTime;
	
	public static TheCharacter readBVH(String path, String encoding){
		BVH_Parser.Bvh bvh = null;
		try{
			BVH_Parser parser = new BVH_Parser();
			
			bvh = parser.parseBvh(new File(path),encoding);
		}catch(Exception e){
			System.out.println(e);
		}
		
		jointMap = new HashMap<String,Joint>();
		
		/** Construct motion information **/
		BVH_Parser.Motion motion = (BVH_Parser.Motion)(bvh.motion());
		BVH_Parser.Numbers nums = (BVH_Parser.Numbers)(motion.numbers());
		
		motion_data = getMotionData(nums);
		
		
		/** Construct joints **/
		
		BVH_Parser.Root root_node = (BVH_Parser.Root)(bvh.root());
		BVH_Parser.Components cos = root_node.coms();
		
		Root root_joint = new Root(getIdentifier(root_node.name()));
		jointMap.put(root_joint.name, root_joint);
		
		int numOfChannel = setJointInfo(root_joint, cos,0);
		motion_count = numOfChannel;
		
		parseBVH(root_joint, cos.joints());
		
		return new TheCharacter(root_joint,jointMap);
	}
	private static String getIdentifier(BVH_Parser.Node id){
		BVH_Parser.Token token = (BVH_Parser.Token)id;
		return (String)(token.getImage());
	}
	private static double getNumericalData(BVH_Parser.Node num){
		if(num instanceof BVH_Parser.Plus){
			BVH_Parser.Plus plus = (BVH_Parser.Plus)num;

			BVH_Parser.Token token = (BVH_Parser.Token)(plus.data());
			return Double.parseDouble((String)(token.getImage()));
		}else{
			BVH_Parser.Minus minus = (BVH_Parser.Minus)num;

			BVH_Parser.Token token = (BVH_Parser.Token)(minus.data());
			return -Double.parseDouble((String)(token.getImage()));	
		}
		
	}
	private static double[] getNumbers(BVH_Parser.Node n){
		if(n instanceof BVH_Parser.Numbers){
			BVH_Parser.Numbers numbers = (BVH_Parser.Numbers)n;

			BVH_Parser.Token token = (BVH_Parser.Token)(numbers.d());
			String num_img = token.getImage();
			String[] nums = num_img.split("[,\\s]+");
			double[] result = new double[nums.length];
			for(int i=0;i<result.length;i++){
				result[i] = Double.parseDouble(nums[i]);
			}
			return result;
		}
		return null;
	}

	public static double[][] getMotionData(BVH_Parser.Node n){
		if(n instanceof BVH_Parser.Numbers){
			BVH_Parser.Numbers numbers = (BVH_Parser.Numbers)n;

			BVH_Parser.Token token = (BVH_Parser.Token)(numbers.d());
			String num_img = token.getImage();
			String[] nums = num_img.split("\n");
			double[][] result = new double[nums.length-1][];
			
			
			/** Frame time **/
			frameTime = Double.parseDouble(nums[0]);
			for(int i=0;i<result.length;i++){
				int be = 0;
				while(!Character.isDigit(nums[i+1].charAt(be))) be++;
				String[] nums_line = nums[i+1].substring(be).split("[,\\s]+");
				result[i] = new double[nums_line.length];
				for(int j=0;j<nums_line.length;j++)
					result[i][j] = Double.parseDouble(nums_line[j]);
			}
			return result;
		}
		return null;
	}

	private static void parseBVH(Joint root, BVH_Parser.Node n){
		if(n instanceof BVH_Parser.Joints){
			BVH_Parser.Joints joints = (BVH_Parser.Joints)n;
			
			while(joints != null){
				parseBVH(root,joints.joint());
				joints = joints.next();
			}
		}else if(n instanceof BVH_Parser.Joint){
			BVH_Parser.Joint j = (BVH_Parser.Joint)n;
			BVH_Parser.Components cos = (BVH_Parser.Components)(j.coms());
			
			Joint joint = new Joint(getIdentifier(j.name()));
			jointMap.put(joint.name, joint);

			//System.out.println(joint.name);
			root.add(joint);
			motion_count += setJointInfo(joint, j.coms(),motion_count);
			
			parseBVH(joint,cos.joints());
			
		}else if(n instanceof BVH_Parser.EndSite){
			BVH_Parser.EndSite end = (BVH_Parser.EndSite)n;
			BVH_Parser.Offset offset = (BVH_Parser.Offset)(end.offset());
			
			EndSite end_site = new EndSite("EndSite");
			end_site.setOffset(getNumbers(offset.numbers()));
			root.add(end_site);
			
		}
	}

	private static void parseBVHForFrameDataHeader(StringBuilder frameHeader,Joint root, BVH_Parser.Node n){
		if(n instanceof BVH_Parser.Joints){
			BVH_Parser.Joints joints = (BVH_Parser.Joints)n;

			while(joints != null){
				parseBVHForFrameDataHeader(frameHeader,root,joints.joint());
				joints = joints.next();
			}
		}else if(n instanceof BVH_Parser.Joint){
			BVH_Parser.Joint j = (BVH_Parser.Joint)n;
			BVH_Parser.Components cos = (BVH_Parser.Components)(j.coms());

			Joint joint = new Joint(getIdentifier(j.name()));
			jointMap.put(joint.name, joint);

			List<String> jointsHeader = getJointList(j.coms());
			for(int i=0;i<jointsHeader.size();i++) frameHeader.append(joint.name+"_"+jointsHeader.get(i)+",");

			parseBVHForFrameDataHeader(frameHeader,joint,cos.joints());
		}else if(n instanceof BVH_Parser.EndSite){
			BVH_Parser.EndSite end = (BVH_Parser.EndSite)n;
			BVH_Parser.Offset offset = (BVH_Parser.Offset)(end.offset());

			EndSite end_site = new EndSite("EndSite");
			end_site.setOffset(getNumbers(offset.numbers()));
			root.add(end_site);

		}
	}
	private static List<String> getJointList(BVH_Parser.Node coms){
		BVH_Parser.Components cos = (BVH_Parser.Components)coms;
		BVH_Parser.Channels channel = (BVH_Parser.Channels)(cos.channels());
		BVH_Parser.KindsOfChannel kchs = (BVH_Parser.KindsOfChannel)(channel.kinds());

		ArrayList<String> chs = new ArrayList<String>();
		while(kchs != null){
			chs.add(getIdentifier(kchs.d1()));
			kchs = kchs.next();
		}

		return chs;
	}

	private static int setJointInfo(Joint joint,BVH_Parser.Node coms, int begin){
		BVH_Parser.Components cos = (BVH_Parser.Components)coms;
		BVH_Parser.Offset offset = (BVH_Parser.Offset)(cos.offset());
		BVH_Parser.Channels channel = (BVH_Parser.Channels)(cos.channels());
		BVH_Parser.KindsOfChannel kchs = (BVH_Parser.KindsOfChannel)(channel.kinds());
		
		ArrayList<String> chs = new ArrayList<String>();
		while(kchs != null){
			chs.add(getIdentifier(kchs.d1()));
			kchs = kchs.next();
		}
		joint.setChannelInfo(chs,motion_data, begin);
		joint.setOffset(getNumbers(offset.numbers()));
		
		return chs.size();
	}

	public static BVH_Parser.Motion readMotion(String path, String encoding) {
		BVH_Parser.Bvh bvh = null;
		try {
			BVH_Parser parser = new BVH_Parser();

			bvh = parser.parseBvh(new File(path), encoding);
		} catch (Exception e) {
			System.out.println(e);
		}

		jointMap = new HashMap<String, Joint>();

		BVH_Parser.Root root_node = (BVH_Parser.Root)(bvh.root());
		BVH_Parser.Components cos = root_node.coms();
		Root root_joint = new Root(getIdentifier(root_node.name()));
		jointMap.put(root_joint.name, root_joint);

		/** Construct motion information **/
		return (BVH_Parser.Motion) (bvh.motion());
	}

	public static String getFrameDataHeader(String path, String encoding){
		BVH_Parser.Bvh bvh = null;
		try {
			BVH_Parser parser = new BVH_Parser();

			bvh = parser.parseBvh(new File(path), encoding);
		} catch (Exception e) {
			System.out.println(e);
		}

		BVH_Parser.Root root_node = (BVH_Parser.Root)(bvh.root());
		BVH_Parser.Components cos = root_node.coms();
		Root root_joint = new Root(getIdentifier(root_node.name()));

		StringBuilder frameHeader = new StringBuilder();

		List<String> jointsHeader = getJointList(root_node.coms());
		for(int i=0;i<jointsHeader.size();i++) frameHeader.append(root_node.name().getImage()+"_"+jointsHeader.get(i)+",");


		parseBVHForFrameDataHeader(frameHeader,root_joint, cos.joints());

		return frameHeader.toString().substring(0,frameHeader.length()-1);
	}
}