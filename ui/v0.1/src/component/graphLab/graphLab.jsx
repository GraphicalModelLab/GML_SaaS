import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import PopupMessage from './../popupMessage/popupMessage';
import Graph from './graph';
import NodePropertyView from './graphProperty/nodePropertyView';
import GraphSaveView from './graphSave/graphSaveView';
import ModelHistoryDialogWithSearch from './../modelHistory/modelHistoryDialogWithSearch';
import AddCustomNodeDialog from './graphComponent/addCustomNodeDialog/addCustomNodeDialog';
import ReactTooltip from 'react-tooltip';
import GraphTestResult from './graphTestResult/graphTestResult';

export default class GraphicalDesign extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

       // downloadLink
        this.state = {
            downloadLink: "",
            downloadContent: "",
            analyzingTarget: [],
            algorithms: [],

            modelid: "",
            modelname: "",
            modeltag: "",
            modeldescription: "",

            modelparameter: [
            ],
            evaluationMethod: [
            ]
        };

        this.onDropAttributeImport = this.onDropAttributeImport.bind(this);
        this.onDropAnalyzing = this.onDropAnalyzing.bind(this);
        this.onDropTraining = this.onDropTraining.bind(this);
        this.showNodePropertyView = this.showNodePropertyView.bind(this);
        this.save = this.save.bind(this);
        this.saveCallBack = this.saveCallBack.bind(this);
        this.setup = this.setup.bind(this);
        this.clear = this.clear.bind(this);

        this.addNode = this.addNode.bind(this);
        this.addEdge = this.addEdge.bind(this);

        this.openPreviousTestedGraph = this.openPreviousTestedGraph.bind(this);
        this.addCustomNodeToCanvas = this.addCustomNodeToCanvas.bind(this);

        this.showAddCustomNodeDialog = this.showAddCustomNodeDialog.bind(this);

        this.changeAlgorithm = this.changeAlgorithm.bind(this);
        this.updateColor = this.updateColor.bind(this);
    }

    showAddCustomNodeDialog(){
        this.refs.addCustomNodeToCanvas.openModal();
    }

    addCustomNodeToCanvas(label, x,y){
        this.refs.graph.addNode(label, x, y, false, []);
    }

    addNode(label,x,y,disable,properties){
        this.refs.graph.addNode(label, x, y, disable, properties);

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

        this.clear();
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

        var self = this;
        $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/model/algorithm/list?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid(),
                    type : "get",
                    headers : {
                        Authorization: "Bearer "+auth.getToken()
                    },
                    success: function(response) {
                        console.log("algorithm list");
                        console.log(response);
                        if(response.body.code == 401){
                            auth.logout();
                        }

                        self.setState({
                            algorithms : response.body.modelAlgorithmIds
                        });

                        if(self.props.location.state){
                            if(self.props.location.state.graphInfo){
                                //alert("Found Graph Info");
                                self.setup(self.props.location.state.graphInfo);
                            }
                        }
                    },
                    error: function (request, status, error) {
                        alert("error");
                    console.log(request.responseText);
                        console.log(status);
                        console.log(error);
                    }
        });
    }
    clear(){
        var graph = this.refs.graph;
        graph.clearSvgPane();

        this.setState({
            analyzingTarget : []
        });
    }
    setup(graphInfo){
        var graph = this.refs.graph;

        if(graph){
            if(graphInfo.algorithm){
                this.refs.algorithm.value = graphInfo.algorithm;
                this.changeAlgorithm();
            }
            var self = this;
            graphInfo.nodes.forEach(function(entry) { self.addNode(entry.label, entry.x, entry.y, entry.disable, entry.properties); });
            graphInfo.edges.forEach(function(entry) { self.addEdge(entry.label1,entry.label2, entry.x1, entry.y1, entry.x2, entry.y2, false); });

            this.refs.nodePropertyView.addProperties(graphInfo.commonProperties);
        }

        this.setState({
                    modelid: graphInfo.modelid,
                    modelname: graphInfo.modelname,
                    modeltag: graphInfo.modeltag,
                    modeldescription: graphInfo.modeldescription
        });
    }

    onDropTraining(acceptedFiles, rejectedFiles){

        if(this.state.modelid.length > 0){
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

                var graph = {
                            modelid: self.state.modelid,
                            modelname: self.state.modelname,
                            modeltag: self.state.modeltag,
                            modeldescription: self.state.modeldescription,
                            userid: auth.getUserid(),
                            algorithm: self.refs.algorithm.value,
                            nodes: self.refs.graph.getNodes(),
                            edges: self.refs.graph.getEdges(),
                            commonProperties: self.refs.nodePropertyView.getProperties()
                };

                var data = {
                        companyid: auth.getCompanyid(),
                        userid:auth.getUserid(),
                        companyid: auth.getCompanyid(),
                        graph: graph,
                        datasource: data,
                        code:10
                };

                 $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/training",
                    type : "post",
                    data : JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    headers : {
                                Authorization: "Bearer "+auth.getToken()
                    },
                    success: function(response) {
                        if(response.body.code == 401){
                            auth.logout();
                        }
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
        }else{
            //please give a name to a graph at first
            this.refs.graphSaveView.openModal("Save the model before training","","","");
        }
    }

    onDropAnalyzing(acceptedFiles, rejectedFiles){
            var self = this;

            self.refs.loading.openModal();
            self.refs.popupMessage.showMessage("now testing...");

            var formData = new FormData();
            formData.append('file_1', acceptedFiles[0]);
            var targetLabel = this.refs.analyzingTarget.value;
            var evaluationMethod = this.refs.evaluationMethod.value;

            var graph = {
                modelid: self.state.modelid,
                modelname: self.state.modelname,
                modeltag: self.state.modeltag,
                modeldescription: self.state.modeldescription,
                userid: auth.getUserid(),
                algorithm: self.refs.algorithm.value,
                nodes: self.refs.graph.getNodes(),
                edges: self.refs.graph.getEdges(),
                commonProperties: self.refs.nodePropertyView.getProperties()
            };

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
                        companyid: auth.getCompanyid(),
                        graph: graph,
                        evaluationMethod: evaluationMethod,
                        testsource: data,
                        targetLabel: targetLabel,
                        code:10
                };

                console.log("Training start");
                console.log(JSON.stringify(data));
                 $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/test",
                    type : "post",
                    data : JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    headers : {
                        Authorization: "Bearer "+auth.getToken()
                    },
                    success: function(response) {
                        if(response.body.code == 401){
                            auth.logout();
                        }

                        var jsonResponse = JSON.parse(response.body.accuracy)

                        self.refs.testResult.openModal(jsonResponse.accuracy,jsonResponse.evaluationMethod);
                    },
                    error: function (request, status, error) {
                        alert("failed to do testing. Contact Administrator");
                        console.log(status);
                        console.log(error);
                    },
                 }).done((data, textStatus, jqXHR) => {
                    self.refs.loading.closeModal();
                    self.refs.popupMessage.closeMessage("finished testing !");

                    console.log("done testing");
                    console.log(data);
                 })
            })
        }

    showNodePropertyView(){
       this.refs.nodePropertyView.openModal();
    }

    save(){
       this.refs.graphSaveView.openModal("",this.state.modelname,this.state.modeltag,this.state.modeldescription);
    }

    saveCallBack(modelname,modeltag,modeldescription){

        var graph = {
            modelid: modelname,
            modelname: modelname,
            modeltag: modeltag,
            modeldescription: modeldescription,
            userid: auth.getUserid(),
            algorithm: this.refs.algorithm.value,
            nodes: this.refs.graph.getNodes(),
            edges: this.refs.graph.getEdges(),
            commonProperties: this.refs.nodePropertyView.getProperties()
        };

        var data = {
                    companyid: auth.getCompanyid(),
                    userid:auth.getUserid(),
                    graph: graph,
                    code:10
        };

        this.setState({
            downloadLink: "DownloadLink",
            downloadContent: "data:text/csv;charset=utf-8,"+JSON.stringify(graph),
            modelid: graph.modelid,
            modelname: graph.modelname,
            modeltag: graph.modeltag,
            modeldescription: graph.modeldescription
        });

        $.ajax({
            url  : "../commonModules/php/modules/GML.php/gml/model/save",
            type : "post",
            data : JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            headers : {
                        Authorization: "Bearer "+auth.getToken()
            },
            success: function(response) {
                console.log("success for save");
                console.log(response);
                if(response.body.code == 401){
                    auth.logout();
                }
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

    openPreviousTestedGraph(){

        this.refs.modelHistoryDialogWithSearch.openModal(auth.getUserid(),this.state.modelid);
    }

    changeAlgorithm(){

        var self = this;
        $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/model/parameter?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid()+"&userid="+auth.getUserid()+"&algorithm="+this.refs.algorithm.value,
                    type : "get",
                    headers : {
                        Authorization: "Bearer "+auth.getToken()
                    },
                    success: function(response) {
                        console.log("fetch parameter");
                        console.log(response);
                        if(response.body.code == 401){
                            auth.logout();
                        }
                        var modelparameter = [];
                        var modelEvaluationMethod = [];

                        response.body.parameter.forEach(function(entry) {
                            modelparameter.push({
                                label: entry
                            });
                        });

                        response.body.evaluationMethod.forEach(function(entry) {
                            modelEvaluationMethod.push(entry);
                        });

                        console.log("setup params");
                        console.log(modelEvaluationMethod);
                        self.setState({
                            modelparameter : modelparameter,
                            evaluationMethod: modelEvaluationMethod
                        });
                    },
                    error: function (request, status, error) {
                        alert("error");
                    console.log(request.responseText);
                        console.log(status);
                        console.log(error);
                    }
        });
    }
    updateColor(){
        alert("updated Color");
    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.graphLabMenu}>
                        <div className={styles.graphLabMenuCalculationModelItem}>
                            <select ref="algorithm" className={styles.graphLabMenuItemCalculationSelect} onChange={this.changeAlgorithm}>
                                <option value="" disabled selected>Select your model</option>
                                    { this.state.algorithms.map((d, idx) => {
                                        return <option value={d} key={"evaluation"+d}>{d}</option>
                                    })}
                            </select>
                        </div>
                        <div onClick={this.showNodePropertyView} className={styles.graphLabMenuItem}><img src="./../icon/graphlab_menu_icons/commonSetting.png" className={styles.graphLabMenuIcon} data-tip="Setup Common Property for all nodes"/></div>
                        <NodePropertyView label="All Nodes" ref="nodePropertyView" modelparameter={this.state.modelparameter} />
                        <Dropzone
                            className={styles.graphLabMenuItem}
                            onDrop={this.onDropAttributeImport}
                            accept="text/csv" >
                            <div>
                                <img src="./../icon/graphlab_menu_icons/importAttrs.png" className={styles.graphLabMenuIcon} data-tip="Import Attribute Information from CSV file"/>
                            </div>
                        </Dropzone>
                        <Dropzone
                            className={styles.graphLabMenuItemTraining}
                            onDrop={this.onDropTraining}
                            accept="text/csv" >
                            <div>
                                <img src="./../icon/graphlab_menu_icons/training.jpg" className={styles.graphLabMenuIcon} data-tip="Training Model. Drop File Here or Click"/>
                            </div>
                        </Dropzone>
                        <div className={styles.graphLabMenuItemDropAnalyzingBox}>
                            <Dropzone
                                className={styles.graphLabMenuItemDropAnalyzing}
                                onDrop={this.onDropAnalyzing}
                                accept="text/csv"
                                data-tip="Evaluate Model. Drop File Here Or Click"
                                >
                                <div>
                                   <img src="./../icon/graphlab_menu_icons/test.png" className={styles.graphLabMenuIcon} />
                                </div>
                            </Dropzone>
                            <div className={styles.graphLabMenuItemAnalyzingTargetBox}>
                            <select ref="evaluationMethod" className={styles.graphLabMenuItemAnalyzingTarget}>
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
                        </div>
                        <GraphSaveView saveCallBack={this.saveCallBack} ref="graphSaveView" />
                        <AddCustomNodeDialog addCustomNode={this.addCustomNodeToCanvas} ref="addCustomNodeToCanvas" />

                        <div onClick={this.save} className={styles.graphLabMenuItemSave}><img src="./../icon/graphlab_menu_icons/save.png" className={styles.graphLabMenuIcon} data-tip="Save Model"/><br/><a className={styles.graphLabMenuItemDownloadLink} href={this.state.downloadContent} download="graph.json" >{this.state.downloadLink}</a></div>
                        <div onClick={this.openPreviousTestedGraph} className={styles.graphLabMenuItem}><img src="./../icon/graphlab_menu_icons/testHistory.png" className={styles.graphLabMenuIcon} data-tip="History of Testing Model"/></div>
                        <div onClick={this.showAddCustomNodeDialog} className={styles.graphLabMenuItemLast}><img src="./../icon/graphlab_menu_icons/addNode.png" className={styles.graphLabMenuIcon} data-tip="Add Node to Canvas"/></div>
                    </div>
                    <Graph ref="graph" items={[]} modelparameter={this.state.modelparameter} />
                </div>
                <Loading ref="loading"/>
                <PopupMessage ref="popupMessage"/>
                <ModelHistoryDialogWithSearch ref="modelHistoryDialogWithSearch" setup={this.setup} clear={this.clear}/>
                <ReactTooltip />
                <GraphTestResult ref="testResult"/>
            </div>
           )
    }
}