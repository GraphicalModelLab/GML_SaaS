import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import Graph from './graph';
import NodePropertyView from './graphProperty/nodePropertyView'
import GraphSaveView from './graphSave/graphSaveView'

export default class GraphicalDesign extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

       // downloadLink
        this.state = {
            downloadLink: "",
            downloadContent: ""
        };

        this.onDropAttributeImport = this.onDropAttributeImport.bind(this);
        this.onDropAnalyzing = this.onDropAnalyzing.bind(this);
        this.showNodePropertyView = this.showNodePropertyView.bind(this);
        this.save = this.save.bind(this);
        this.saveCallBack = this.saveCallBack.bind(this);
        this.setup = this.setup.bind(this);

    }
    onDropAttributeImport(acceptedFiles, rejectedFiles){
        var reader = new FileReader();
        var graph = this.refs.graph;
        graph.clearSvgPane();
        reader.onload = function(e) {
            var text = reader.result;                 // the entire file

            var firstLine = text.split(/\r\n|\r|\n/); // first line

            console.log("add attributes:"+firstLine[0]);
            var index = 0;
            firstLine[0].split(',').forEach(function(entry) {
                graph.addNode(entry, index * 100 + 20, 100);

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

            graphInfo.nodes.forEach(function(entry) { graph.addNode(entry.label, entry.x, entry.y); });
            graphInfo.edges.forEach(function(entry) { graph.addEdge(entry.label1,entry.label2, entry.x1, entry.y1, entry.x2, entry.y2); });
        }
    }

    onDropAnalyzing(acceptedFiles, rejectedFiles){
        var formData = new FormData();
        formData.append('file_1', acceptedFiles[0]);

        $.ajax({
                url  : "../commonModules/php/modules/Uploader.php",
                type : "POST",
                data : formData,
                cache       : false,
                contentType : false,
                processData : false,
                dataType    : "text",
                success: function() {
                                                alert("Success!");
                },
                error: function (request, status, error) {
                                         alert("error");
                                         console.log(status);
                                         console.log(error);

                },
        }).done((data, textStatus, jqXHR) => {
            alert(data);
            var data = {
                    companyid: auth.getCompanyid(),
                    userid:auth.getUserid(),
                    token: auth.getToken(),
                    companyid: auth.getCompanyid(),
                    algorithm: "test",
                    datasource: data,
                    nodes: this.refs.graph.state.nodes,
                    edges: this.refs.graph.state.edges,
                    code:10
            };

            alert("POST to training");
             $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/training",
                type : "post",
                data : JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(response) {
                    alert("succeed to training");
                    console.log("success for traininig");
                    console.log(response);

                },
                error: function (request, status, error) {
                                                                     alert("error");
                                                                     console.log(status);
                                                                     console.log(error);

                },
             }).done((data, textStatus, jqXHR) => {

                                                                     alert("done");
                                                                     console.log(data);
                                                                     console.log(textStatus);
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
        var nodeArray = [];
        for(let index in this.refs.graph.state.nodes){
            nodeArray.push({
                label: this.refs.graph.state.nodes[index].label,
                disable: false,
                x: this.refs.graph.refs[this.refs.graph.state.nodes[index].label].state.x,
                y: this.refs.graph.refs[this.refs.graph.state.nodes[index].label].state.y
            });
        }

        var graph = {
            modelname: modelName,
            modeltag: modelTag,
            modeldescription: modelDescription,
            userid: auth.getUserid(),
            algorithm: this.refs.algorithm.value,
            nodes: nodeArray,
            edges: this.refs.graph.state.edges
        };

        var data = {
                    companyid: auth.getCompanyid(),
                    userid:auth.getUserid(),
                    token: auth.getToken(),
                    modelid: "test",
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
                                <br/>属性情報<br/>ファイル<br/>ドロップ
                            </div>
                        </Dropzone>
                        <Dropzone
                            className={styles.graphLabMenuItem}
                            onDrop={this.onDropAnalyzing}
                            accept="text/csv" >
                            <div>
                                <br/>学習データ<br/>ファイル<br/>ドロップ
                            </div>
                        </Dropzone>
                        <Dropzone
                            className={styles.graphLabMenuItem}
                            onDrop={this.onDropAnalyzing}
                            accept="text/csv" >
                            <div>
                                <br/>解析データ<br/>ファイル<br/>ドロップ
                            </div>
                        </Dropzone>
                         <GraphSaveView saveCallBack={this.saveCallBack} ref="graphSaveView" />

                        <div onClick={this.save} className={styles.graphLabMenuItem}><br/><br/>モデル保存<br/><a className={styles.graphLabMenuItemDownloadLink} href={this.state.downloadContent} download="graph.json" >{this.state.downloadLink}</a></div>
                    </div>
                    <Graph ref="graph" items={[]}/>
                </div>
            </div>
           )
    }
}