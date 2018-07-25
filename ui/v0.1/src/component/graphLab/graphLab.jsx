import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import PopupMessage from './../popupMessage/popupMessage';
import Graph from './graph';
import NodePropertyView from './graphProperty/nodePropertyView'
import GraphSaveView from './graphSave/graphSaveView'

export default class GraphicalDesign extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

       // downloadLink
        this.state = {
            downloadLink: "",
            downloadContent: "",
            evaluationMethod: ["simple", "cross-validation", "precision-recall", "ROC"],
            analyzingTarget: []
        };

        this.onDropAttributeImport = this.onDropAttributeImport.bind(this);
        this.onDropAnalyzing = this.onDropAnalyzing.bind(this);
        this.onDropTraining = this.onDropTraining.bind(this);
        this.showNodePropertyView = this.showNodePropertyView.bind(this);
        this.save = this.save.bind(this);
        this.saveCallBack = this.saveCallBack.bind(this);
        this.setup = this.setup.bind(this);

        this.addNode = this.addNode.bind(this);
        this.addEdge = this.addEdge.bind(this);

    }

    addNode(label,x,y,disable,properties){
        this.refs.graph.addNode(label, x, y, disable, properties);

        console.log("analyzing add node : "+label);
        this.state.analyzingTarget.push(label);
        this.setState({
            analyzingTarget : this.state.analyzingTarget
         });
    }

    addEdge(label1, label2, x1, y1, x2, y2, disable){
        this.refs.graph.addEdge(label1, label2, x1, y1, x2, y2, disable);
    }

    onDropAttributeImport(acceptedFiles, rejectedFiles){
        var reader = new FileReader();
        var graph = this.refs.graph;
        graph.clearSvgPane();
        var self = this;
        reader.onload = function(e) {
            var text = reader.result;                 // the entire file

            var firstLine = text.split(/\r\n|\r|\n/); // first line

            console.log("add attributes:"+firstLine[0]);
            var index = 0;
            firstLine[0].split(',').forEach(function(entry) {
                self.addNode(entry, index * 100 + 20, 100, false, []);

                index += 1;
            });
        }

        reader.readAsText(acceptedFiles[0], 'UTF-8');
    }

    componentDidMount() {
        console.log("component Did mount ");
        console.log(this.props.location);
        if(this.props.location.state){
            if(this.props.location.state.graphInfo){
                //alert("Found Graph Info");
                this.setup(this.props.location.state.graphInfo);
            }
        }
    }

    setup(graphInfo){
        var graph = this.refs.graph;

        if(graph){
            if(graphInfo.algorithm){
                this.refs.algorithm.value = graphInfo.algorithm;
            }
            var self = this;
            graphInfo.nodes.forEach(function(entry) { self.addNode(entry.label, entry.x, entry.y, entry.disable, entry.properties); });
            graphInfo.edges.forEach(function(entry) { self.addEdge(entry.label1,entry.label2, entry.x1, entry.y1, entry.x2, entry.y2, false); });

            this.refs.nodePropertyView.addProperties(graphInfo.commonProperties);
        }
    }

    onDropTraining(acceptedFiles, rejectedFiles){
        var formData = new FormData();
        formData.append('file_1', acceptedFiles[0]);


        var self = this;

        self.refs.loading.openModal();
        self.refs.popupMessage.showMessage("now training...");
        $.ajax({
                url  : "../commonModules/php/modules/Uploader.php",
                type : "POST",
                data : formData,
                cache       : false,
                contentType : false,
                processData : false,
                dataType    : "text",
                success: function() {

                },
                error: function (request, status, error) {
                    alert("failed to upload csv file to server. Contact Administrator");
                    console.log(status);
                    console.log(error);
                },
        }).done((data, textStatus, jqXHR) => {
            var data = {
                    companyid: auth.getCompanyid(),
                    userid:auth.getUserid(),
                    token: auth.getToken(),
                    companyid: auth.getCompanyid(),
                    algorithm: "test",
                    datasource: data,
                    //nodes: this.refs.graph.state.nodes,
                    //edges: this.refs.graph.state.edges,
                    nodes: this.refs.graph.getNodes(),
                    edges: this.refs.graph.getEdges(),
                    commonProperties: this.refs.nodePropertyView.getProperties(),
                    code:10
            };

             $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/training",
                type : "post",
                data : JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(response) {

                },
                error: function (request, status, error) {
                    alert("Failed to train the model. Contact Administrator");
                    console.log(status);
                    console.log(error);
                },
             }).done((data, textStatus, jqXHR) => {
                self.refs.loading.closeModal();
                self.refs.popupMessage.closeMessage("finished training !");
             });
        })
    }

    onDropAnalyzing(acceptedFiles, rejectedFiles){
            var self = this;

            self.refs.loading.openModal();
            self.refs.popupMessage.showMessage("now testing...");

            var formData = new FormData();
            formData.append('file_1', acceptedFiles[0]);
            var targetLabel = this.refs.analyzingTarget.value;
            $.ajax({
                    url  : "../commonModules/php/modules/Uploader.php",
                    type : "POST",
                    data : formData,
                    cache       : false,
                    contentType : false,
                    processData : false,
                    dataType    : "text",
                    success: function() {
                    },
                    error: function (request, status, error) {
                        alert("failed to upload files for testing");
                        console.log(status);
                        console.log(error);
                    },
            }).done((data, textStatus, jqXHR) => {
                var data = {
                        companyid: auth.getCompanyid(),
                        userid:auth.getUserid(),
                        token: auth.getToken(),
                        companyid: auth.getCompanyid(),
                        algorithm: "test",
                        testsource: data,
                        gmlId: "ttt",
                        targetLabel: targetLabel,
                        code:10
                };

                 $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/test",
                    type : "post",
                    data : JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    success: function(response) {

                    },
                    error: function (request, status, error) {
                        alert("failed to do testing. Contact Administrator");
                        console.log(status);
                        console.log(error);
                    },
                 }).done((data, textStatus, jqXHR) => {
                    self.refs.loading.closeModal();
                    self.refs.popupMessage.closeMessage("finished testing !");
                 })
            })
        }

    showNodePropertyView(){
       this.refs.nodePropertyView.openModal();
    }

    save(){
       this.refs.graphSaveView.openModal();
    }

    saveCallBack(modelName,modelTag,modelDescription){

        var graph = {
            modelname: modelName,
            modeltag: modelTag,
            modeldescription: modelDescription,
            userid: auth.getUserid(),
            algorithm: this.refs.algorithm.value,
            nodes: this.refs.graph.getNodes(),
            edges: this.refs.graph.getEdges(),
            commonProperties: this.refs.nodePropertyView.getProperties()
        };

        var data = {
                    companyid: auth.getCompanyid(),
                    userid:auth.getUserid(),
                    token: auth.getToken(),
                    modelid: modelName,
                    graph: JSON.stringify(graph),
                    code:10
        };

        this.setState({
            downloadLink: "ダウンロードリンク",
            downloadContent: "data:text/csv;charset=utf-8,"+JSON.stringify(graph)
        });

        $.ajax({
            url  : "../commonModules/php/modules/GML.php/gml/model/save",
            type : "post",
            data : JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            success: function(response) {
                console.log("success for save");
                console.log(response);
            },
            error: function(request, status, error) {
                alert("error");
                console.log(request);
                console.log(status);
                console.log(error);
            }
        });


        var self = this;
        setTimeout(function(){
            self.setState({
                    downloadLink: "",
                    downloadContent: ""
            });
        }, 5000);
    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.graphLabMenu}>
                        <div className={styles.graphLabMenuCalculationModelItem}><br/>計算モデル<br/><br/><select ref="algorithm" className={styles.graphLabMenuItemCalculationSelect}>
                                                                                                                      <option value="" disabled selected>Select your model</option>
                                                                                                                      <option value="model1">Freq(Mul & Norm)</option>
                                                                                                                      <option value="model2">Only Multinomial</option>
                                                                                                                    </select></div>
                        <div onClick={this.showNodePropertyView} className={styles.graphLabMenuItem}><br/>共通Node<br/>プロパティ<br/>設定 </div>
                        <NodePropertyView label="全ノード" ref="nodePropertyView" />
                        <Dropzone
                            className={styles.graphLabMenuItem}
                            onDrop={this.onDropAttributeImport}
                            accept="text/csv" >
                            <div>
                                <br/>属性情報<br/>ファイル<br/>クリック<br/>(ドロップ)
                            </div>
                        </Dropzone>
                        <Dropzone
                            className={styles.graphLabMenuItem}
                            onDrop={this.onDropTraining}
                            accept="text/csv" >
                            <div>
                                <br/>学習データ<br/>ファイル<br/>クリック<br/>(ドロップ)
                            </div>
                        </Dropzone>
                        <div className={styles.graphLabMenuItemDropAnalyzingBox}>
                            <Dropzone
                                className={styles.graphLabMenuItemDropAnalyzing}
                                onDrop={this.onDropAnalyzing}
                                accept="text/csv" >
                                <div>
                                   <br/> 解析データ
                                </div>
                            </Dropzone>
                            <select ref="analyzingTarget" className={styles.graphLabMenuItemAnalyzingTarget}>
                                <option value="" disabled selected>Select Evaluation Method</option>
                                { this.state.evaluationMethod.map((d, idx) => {
                                    return <option value={d} key={"evaluation"+d}>{d}</option>
                                })}
                            </select>
                            <select ref="analyzingTarget" className={styles.graphLabMenuItemAnalyzingTarget}>
                                <option value="" disabled selected>Select Target</option>
                                { this.state.analyzingTarget.map((d, idx) => {
                                    return <option value={d} key={"option"+d}>{d}</option>
                                })}
                            </select>
                        </div>
                        <GraphSaveView saveCallBack={this.saveCallBack} ref="graphSaveView" />

                        <div onClick={this.save} className={styles.graphLabMenuItem}><br/><br/>モデル保存<br/><a className={styles.graphLabMenuItemDownloadLink} href={this.state.downloadContent} download="graph.json" >{this.state.downloadLink}</a></div>
                    </div>
                    <Graph ref="graph" items={[]}/>
                </div>
                <Loading ref="loading"/>
                <PopupMessage ref="popupMessage"/>
            </div>
           )
    }
}