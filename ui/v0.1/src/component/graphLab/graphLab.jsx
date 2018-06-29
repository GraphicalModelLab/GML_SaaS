import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import Graph from './graph';
import NodePropertyView from './graphProperty/nodePropertyView'

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
    }
    onDropAttributeImport(acceptedFiles, rejectedFiles){
        var reader = new FileReader();
        var graph = this.refs.graph;
        graph.clearSvgPane();
        reader.onload = function(e) {
            var text = reader.result;                 // the entire file

            var firstLine = text.split(/\r\n|\r|\n/); // first line

            console.log("add attributes:"+firstLine[0]);
            firstLine[0].split(',').forEach(function(entry) {
                graph.addNode(entry);
            });
        }

        reader.readAsText(acceptedFiles[0], 'UTF-8');
    }

    onDropAnalyzing(acceptedFiles, rejectedFiles){
        var formData = new FormData();
        formData.append('file_1', acceptedFiles[0]);

        console.log("on Drop Analyzing");
        console.log(this.refs.graph.state.nodes);
        console.log(this.refs.graph.state.edges);

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

        console.log("graphicalb to save model");
        console.log(JSON.stringify(data));
        $.ajax({
            url  : "../commonModules/php/modules/GML.php/gml/model/save",
            type : "post",
            data : JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            success: function(response) {
                alert("succeed to save");
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
                        <div onClick={this.save} className={styles.graphLabMenuItem}><br/><br/>モデル保存<br/><a className={styles.graphLabMenuItemDownloadLink} href={this.state.downloadContent} download="graph.json" >{this.state.downloadLink}</a></div>
                    </div>
                    <Graph ref="graph" items={[]}/>
                </div>
            </div>
           )
    }
}