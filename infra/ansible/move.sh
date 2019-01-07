BASEDIR="/Users/itomao/git/GML_SaaS/infra/ansible"

echo $BASEDIR

cd ~/git/GML_SaaS/ui/v0.1/

echo "move to ~/git/GML_SaaS/ui/v0.1/"

echo "$BASEDIR/roles/gml/files/dist.zip"
zip -r $BASEDIR/roles/gml/files/dist.zip ./dist

zip -r $BASEDIR/roles/gml/files/company_ui.zip company

zip -r $BASEDIR/roles/gml/files/icon.zip icon
zip -r $BASEDIR/roles/gml/files/commonData.zip commonData
zip -r $BASEDIR/roles/gml/files/commonModules.zip commonModules
zip -r $BASEDIR/roles/gml/files/commonLibs.zip commonLibs

cd $BASEDIR

cp ~/git/GML_SaaS/gmb/auth/target/universal/auth-0.1-SNAPSHOT.zip $BASEDIR/roles/gml/files/auth.zip
cp ~/git/GML_SaaS/gmb/gml/target/universal/gml-0.1-SNAPSHOT.zip $BASEDIR/roles/gml/files/company.zip

cd $BASEDIR