import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import GraphLabRecord from './../graphLabRepository/graphLabRecord';
import { withRouter } from 'react-router';

// Tree List  https://www.sozailab.jp/sozai/detail/6152/
//            https://www.google.co.jp/imgres?imgurl=https%3A%2F%2Fpds.exblog.jp%2Fpds%2F1%2F200810%2F13%2F45%2Fd0094245_1032524.gif&imgrefurl=https%3A%2F%2Fpopachi.exblog.jp%2F8757735%2F&docid=c13zelLPL4D8aM&tbnid=zFXSOSvu7c3ZTM%3A&vet=10ahUKEwjn44y9pf_bAhULoJQKHaUUBQwQMwiZASgAMAA..i&w=416&h=414&bih=551&biw=1085&q=%E3%83%AA%E3%83%B3%E3%82%B4%E3%80%80%E7%B5%B5&ved=0ahUKEwjn44y9pf_bAhULoJQKHaUUBQwQMwiZASgAMAA&iact=mrc&uact=8
export default class GraphicalLabLocalRepository extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
        this.state = {
            records: [],
            svg_width: window.innerWidth - 17,
            svg_height: window.innerHeight - 100,

            tree_width: 640,
            tree_height: 599,

            selectedModelName: "",
            selectedModelVersion: "",
            selectedModelTimestamp: "",
            selectedModelTag: "",
            selectedModelDescription: "",
            selectedRecordInfo: null
        };
        this.updateDimensions = this.updateDimensions.bind(this);
        this.recordEnterCallBack = this.recordEnterCallBack.bind(this);
        this.openGraph = this.openGraph.bind(this);
        this.setup = this.setup.bind(this);
        this.clear = this.clear.bind(this);
        this.showResult = this.showResult.bind(this);
    }

    componentWillUnmount(){
        window.removeEventListener("resize", this.updateDimensions);
    }
    clear(){
       this.setState({
            records: [],
            selectedModelName: "",
            selectedModelVersion: "",
            selectedModelTimestamp: "",
            selectedModelTag: "",
            selectedModelDescription: "",
            selectedRecordInfo: null
       });
    }

    componentDidMount() {
        window.addEventListener("resize", this.updateDimensions);
        this.clear();
        this.showResult();
    }

    showResult(){
           var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                code:10
            };

            var self = this;
            $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/model/list",
                type : "post",
                data : JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(response) {
                    var modelRecords = [];

                    for(let model of response.body.models) {
                        var modelInfo = JSON.parse(model);
                        modelInfo["x"] = Math.random() * self.state.tree_width - self.state.tree_width/2;
                        modelInfo["y"] = Math.random() * self.state.tree_height - self.state.tree_height/2;

                        modelRecords.push(modelInfo);
                    }

                    self.setState({
                            records: modelRecords
                    });
                },
                error: function (request, status, error) {
                    alert("error");
                    console.log(request);
                    console.log(status);
                    console.log(error);
                }
            });
    }

    setup(models){
        var modelRecords = [];
        for(let modelInfo of models) {
            modelInfo["x"] = this.state.tree_width/2 - Math.random() * this.state.tree_width ;
            modelInfo["y"] = this.state.tree_height/4 - Math.random() * this.state.tree_height/2 ;

            modelRecords.push(modelInfo);
        }

        this.setState({
            records: modelRecords
        });
    }

    updateDimensions(){
            this.setState({
                svg_width: window.innerWidth - 17,
                svg_height: window.innerHeight - 100
            });
    }

    recordEnterCallBack(recordInfo){
            this.setState({
                selectedModelName: recordInfo.modelname,
                selectedModelVersion: recordInfo.modelversion,
                selectedModelTimestamp: recordInfo.timestamp,
                selectedModelTag: recordInfo.modeltag,
                selectedModelDescription: recordInfo.modeldescription,
                selectedRecordInfo: recordInfo
            });
    }

    openGraph(){
        var self = this;

        if(this.state.selectedRecordInfo.modelid){
            alert("open : "+this.state.selectedRecordInfo.modelid);
            console.log(this.state.selectedRecordInfo);
            var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                code:10,
                modelid: this.state.selectedRecordInfo.modelid,
                datetime: this.state.selectedRecordInfo.timestamp
            };
            $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/model/get",
                    type : "post",
                    data : JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    success: function(response) {
                        self.context.router.push({
                            pathname: '/graphLab',
                            state: {
                                graphInfo: JSON.parse(response.body.model)
                            }
                        });
                    },
                    error: function (request, status, error) {
                        alert("error");
                        console.log(request);
                        console.log(status);
                        console.log(error);
                    }
            });
        }else{
            this.context.router.push({
                            pathname: '/graphLab',
                            state: {
                                graphInfo: this.state.selectedRecordInfo
                            }
            });
        }
    }

    render() {
        return (
            <svg className={styles.graphRepositoryTree}　id="svg-graphLabLocalRepository" width={this.state.svg_width} height={this.state.svg_height} xmlns="http://www.w3.org/2000/svg" version="1.1"
            >

                <g>
                    <text x={10} y={50} >モデル名 : {this.state.selectedModelName}</text>
                    <text x={10} y={100} >モデル バージョン : {this.state.selectedModelVersion}</text>
                    <text x={10} y={150} >タイムスタンプ : {this.state.selectedModelTimestamp}</text>
                    <text x={10} y={200} >タグ : {this.state.selectedModelTag}</text>
                    <text x={10} y={250}  width={100} height={200} >詳細 : {this.state.selectedModelDescription}</text>
                </g>

                <g onClick={this.openGraph}>
                <rect x="10" y="300" width="180" height="30" stroke="black" fill="transparent"/>
                <text x="30" y="320" >開く (Click Apple!)</text>
                </g>

                <g>
                <image href="../icon/Tree.png"
                    x={(this.state.svg_width - this.state.tree_width) > 0 ? (this.state.svg_width - this.state.tree_width)/2 : 0}
                    y={0}
                />

               { this.state.records.map((d, idx) => {
                   return <GraphLabRecord clickCallBack={this.openGraph} recordEnterCallBack={this.recordEnterCallBack} key={"record:"+idx} recordInfo={d} x={d.x} y={d.y} coordinate_x={(this.state.svg_width - this.state.tree_width) > 0 ? (this.state.svg_width - this.state.tree_width)/2 + this.state.tree_width/2 : this.state.tree_width/2}  coordinate_y={this.state.tree_height/2} />
               }) }
               </g>
            </svg>
           )
    }
}


GraphicalLabLocalRepository.contextTypes = {
    router: React.PropTypes.object
};