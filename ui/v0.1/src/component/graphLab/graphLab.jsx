/**
 * Copyright (C) 2018 Mao Ito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/graphLab.css';
import auth from "./../auth/auth";
import NodeShape from "./../constant/nodeShape";
import $ from 'jquery';
import Loading from './../loader/loading';
import PopupMessage from './../popupMessage/popupMessage';
import PopupMessageSmallBox from './../popupMessage/popupMessageSmallBox';
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

      modelparameter: [],
      evaluationMethod: [],
      modelSupportedShape: [],

      exploreGraph: true
    };

    this.onDropAttributeImport = this.onDropAttributeImport.bind(this);
    this.onDropAnalyzing = this.onDropAnalyzing.bind(this);
    this.onDropTraining = this.onDropTraining.bind(this);
    this.onDropExplore = this.onDropExplore.bind(this);
    this.exploreGraph = this.exploreGraph.bind(this);
    this.stopExploringGraph = this.stopExploringGraph.bind(this);
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
    this.validateParameters = this.validateParameters.bind(this);
    this.showMessage = this.showMessage.bind(this);
  }

  showMessage(message,timeout){
    this.refs.popupMessageSmallBox.showMessage(message, timeout);
  }

  showAddCustomNodeDialog() {
    this.refs.addCustomNodeToCanvas.openModal(this.state.modelSupportedShape);
  }

  addCustomNodeToCanvas(label, x, y, shape) {
    this.refs.graph.addNode(label, x, y, false, [],shape);
  }

  addNode(label, x, y, disable, properties,shape) {
    this.refs.graph.addNode(label, x, y, disable, properties,shape);

    this.state.analyzingTarget.push(label);
    this.setState({
      analyzingTarget: this.state.analyzingTarget
    });
  }

  addEdge(label1, label2, x1, y1, x2, y2, disable, isDirected) {
    this.refs.graph.addEdge(label1, label2, x1, y1, x2, y2, disable,isDirected);
  }

  onDropAttributeImport(acceptedFiles, rejectedFiles) {
    var reader = new FileReader();

    this.clear();
    var self = this;
    reader.onload = function(e) {
      var text = reader.result; // the entire file

      var firstLine = text.split(/\r\n|\r|\n/); // first line

      var index = 0;
      firstLine[0].split(',').forEach(function(entry) {
        self.addNode(entry, index * 100 + 20, 100, false, [], NodeShape.CIRCLE);

        index += 1;
      });
    }

    reader.readAsText(acceptedFiles[0], 'UTF-8');
  }

  componentDidMount() {
    var self = this;
    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/model/algorithm/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
      type: "get",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        if (response.body.code == 401) {
          auth.logout();
        }

        self.setState({
          algorithms: response.body.modelAlgorithmIds
        });

        if (self.props.location.state) {
          if (self.props.location.state.graphInfo) {
            //alert("Found Graph Info");
            self.setup(self.props.location.state.graphInfo);
          }
        }
      },
      error: function(request, status, error) {
        alert("error");
        console.log(request.responseText);
        console.log(status);
        console.log(error);
      }
    });
  }
  clear() {
    var graph = this.refs.graph;
    graph.clearSvgPane();

    this.setState({
      analyzingTarget: []
    });
  }
  setup(graphInfo) {
    var graph = this.refs.graph;

    if (graph) {
      if (graphInfo.algorithm) {
        this.refs.algorithm.value = graphInfo.algorithm;

        this.changeAlgorithm(graphInfo.algorithm);
      }
      var self = this;

      graphInfo.nodes.forEach(function(entry) {
        self.addNode(entry.label, entry.x, entry.y, entry.disable, entry.properties, entry.shape);
      });

      graphInfo.edges.forEach(function(entry) {
        self.addEdge(entry.label1, entry.label2, entry.x1, entry.y1, entry.x2, entry.y2, entry.disable,entry.isDirected);
      });

      this.refs.nodePropertyView.addProperties(graphInfo.commonProperties);
    }

    this.setState({
      modelid: graphInfo.modelid,
      modelname: graphInfo.modelname,
      modeltag: graphInfo.modeltag,
      modeldescription: graphInfo.modeldescription
    });
  }

  validateParameters() {
    // 1. Initialize Required Properties
    var requiredProperties = new Set();
    for (var index in this.state.modelparameter) {
      requiredProperties.add(this.state.modelparameter[index].label);
    }

    // 2. Check Common Properties
    var commonProperties = this.refs.nodePropertyView.getProperties();
    for (var index in commonProperties) {
      requiredProperties.delete(commonProperties[index].name);
    }

    // 3. Check all nodes to see if they have the required properties
    if (requiredProperties.size > 0) {
      //3. Check Properties of All Nodes
      var nodes = this.refs.graph.getNodes();

      for (var index in nodes) {
        var count = 0;
        var nodeProperty = nodes[index].properties;
        for (var propertyIndex in nodeProperty) {
          if (requiredProperties.has(nodeProperty[propertyIndex].name)) {
            count++;
          }
        }
        if (count < requiredProperties.size) {
          return false;
        }
      }

    }

    return true;
  }

  onDropTraining(acceptedFiles, rejectedFiles) {

    if (!this.refs.algorithm.value) {
      this.showMessage("Could not train the model because an algorithm is not chosen. Please set them up !", 20000);
      return;
    }

    if (!this.validateParameters()) {
      this.showMessage("Could not train the model due to the lack of the required properties. Please set them up !", 20000);
      return;
    }

    if (this.state.modelid.length > 0) {
      var formData = new FormData();
      formData.append('file_1', acceptedFiles[0]);

      var self = this;

      self.refs.loading.openModal();
      self.refs.popupMessage.showMessage("now training...");
      $.ajax({
        url: "../commonModules/php/modules/Uploader.php",
        type: "POST",
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        dataType: "text",
        success: function() {},
        error: function(request, status, error) {
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
          userid: auth.getUserid(),
          companyid: auth.getCompanyid(),
          graph: graph,
          datasource: data,
          code: 10
        };

        $.ajax({
          url: "../commonModules/php/modules/GML.php/gml/training",
          type: "post",
          data: JSON.stringify(data),
          contentType: 'application/json',
          dataType: "json",
          headers: {
            Authorization: "Bearer " + auth.getToken()
          },
          success: function(response) {
            if (response.body.code == 401) {
              auth.logout();
            }
          },
          error: function(request, status, error) {
            alert("Failed to train the model. Contact Administrator");
            console.log(status);
            console.log(error);
          },
        }).done((data, textStatus, jqXHR) => {
          self.refs.loading.closeModal();
          self.refs.popupMessage.closeMessage("finished training !");
        });
      })
    } else {
      //please give a name to a graph at first
      this.refs.graphSaveView.openModal("Save the model before training", "", "", "");
    }

  }

  onDropAnalyzing(acceptedFiles, rejectedFiles) {
    if (!this.refs.algorithm.value) {
      this.showMessage("Could not train the model because an algorithm is not chosen. Please set them up !", 20000);
      return;
    }

    if (!this.validateParameters()) {
      this.showMessage("Could not analyze the data due to the lack of the required properties for the chosen model. Please set them up !", 20000);
      return;
    }

    if (this.state.modelid.length > 0) {
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
          url: "../commonModules/php/modules/Uploader.php",
          type: "POST",
          data: formData,
          cache: false,
          contentType: false,
          processData: false,
          dataType: "text",
          success: function() {},
          error: function(request, status, error) {
            alert("failed to upload files for testing");
            console.log(status);
            console.log(error);
          },
        }).done((data, textStatus, jqXHR) => {

          var data = {
            companyid: auth.getCompanyid(),
            userid: auth.getUserid(),
            companyid: auth.getCompanyid(),
            graph: graph,
            evaluationMethod: evaluationMethod,
            testsource: data,
            targetLabel: targetLabel,
            code: 10
          };

          $.ajax({
            url: "../commonModules/php/modules/GML.php/gml/test",
            type: "post",
            data: JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            timeout : 0,
            headers: {
              Authorization: "Bearer " + auth.getToken()
            },
            success: function(response) {

              if(response.success == true){
                  if (response.body.code == 401) {
                    auth.logout();
                  }

                  var jsonResponse = JSON.parse(response.body.accuracy)

                  self.refs.testResult.openModal(jsonResponse.accuracy, jsonResponse.evaluationMethod);
              }else{
                  self.refs.popupMessage.closeMessage("Error Occurred !");
                  self.showMessage("Contact System Administrator : "+response.error, 20000);
                  self.refs.loading.closeModal();
              }
            },
            error: function(request, status, error) {
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
    }else {
      //please give a name to a graph at first
      this.refs.graphSaveView.openModal("Save the model before testing", "", "", "");
    }
  }

  showNodePropertyView() {
    this.refs.nodePropertyView.openModal();
  }

  save() {
    this.refs.graphSaveView.openModal("", this.state.modelname, this.state.modeltag, this.state.modeldescription);
  }

  saveCallBack(modelname, modeltag, modeldescription) {

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
      userid: auth.getUserid(),
      graph: graph,
      code: 10
    };

    this.setState({
      downloadLink: "DownloadLink",
      downloadContent: "data:text/csv;charset=utf-8," + JSON.stringify(graph),
      modelid: graph.modelid,
      modelname: graph.modelname,
      modeltag: graph.modeltag,
      modeldescription: graph.modeldescription
    });

    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/model/save",
      type: "post",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        if (response.body.code == 401) {
          auth.logout();
        }
      },
      error: function(request, status, error) {
        alert("error");
        console.log(request.responseText);
        console.log(status);
        console.log(error);
      }
    });


    var self = this;
    setTimeout(function() {
      self.setState({
        downloadLink: "",
        downloadContent: ""
      });
    }, 5000);
  }

  openPreviousTestedGraph() {

    this.refs.modelHistoryDialogWithSearch.openModal(auth.getUserid(), this.state.modelid);
  }

  changeAlgorithm(algorithm) {

    var self = this;
    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/model/parameter?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid() + "&userid=" + auth.getUserid() + "&algorithm=" + this.refs.algorithm.value,
      type: "get",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        if (response.body.code == 401) {
          auth.logout();
        }

        if(response.body.code == 1001){
            self.showMessage(
            "The algorithm ("+algorithm+") saved for this graph was not found. Make sure that the corresponding plugin is deployed in gml backend service.",
             20000);

            return;
        }

        var modelparameter = [];
        var modelEvaluationMethod = [];
        var modelSupportedShape = [];

        response.body.parameter.forEach(function(entry) {
          modelparameter.push({
            label: entry
          });
        });

        response.body.evaluationMethod.forEach(function(entry) {
          modelEvaluationMethod.push(entry);
        });

        response.body.supportedShape.forEach(function(entry) {
          modelSupportedShape.push(entry);
        });


        self.setState({
          modelparameter: modelparameter,
          evaluationMethod: modelEvaluationMethod,
          modelSupportedShape: modelSupportedShape
        });
      },
      error: function(request, status, error) {
        alert("error");
        console.log(request.responseText);
        console.log(status);
        console.log(error);
      }
    });
  }
  updateColor() {
    alert("updated Color");
  }

  exploreGraph(self, graph, targetLabel, datasource) {
    var data = {
      companyid: auth.getCompanyid(),
      userid: auth.getUserid(),
      graph: graph,
      targetLabel: targetLabel,
      datasource: datasource,
      code: 10
    };

    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/graph/explore",
      type: "post",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        if (response.body.code == 401) {
          auth.logout();
        }

        self.showMessage("accuracy..." + response.body.accuracy, 10000);

        var graph = JSON.parse(response.body.graph);

        self.clear();
        self.setup(graph);

        if (self.state.exploreGraph) {
          self.exploreGraph(self, graph, targetLabel, datasource);
        } else {
          self.setState({
            exploreGraph: true
          });
        }
      },
      error: function(request, status, error) {
        alert("failed to do testing. Contact Administrator");
        console.log(status);
        console.log(error);
      },
    }).done((data, textStatus, jqXHR) => {
    })
  }

  stopExploringGraph() {
    this.setState({
      exploreGraph: false
    });
  }

  onDropExplore(acceptedFiles, rejectedFiles) {
    var self = this;

    var formData = new FormData();
    formData.append('file_1', acceptedFiles[0]);
    var targetLabel = this.refs.analyzingTarget.value;
    var evaluationMethod = this.refs.evaluationMethod.value;

    self.setState({
      exploreGraph: true
    });

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
      url: "../commonModules/php/modules/Uploader.php",
      type: "POST",
      data: formData,
      cache: false,
      contentType: false,
      processData: false,
      dataType: "text",
      success: function() {},
      error: function(request, status, error) {
        alert("failed to upload files for testing");
        console.log(status);
        console.log(error);
      },
    }).done((data, textStatus, jqXHR) => {
      self.exploreGraph(self, graph, targetLabel, data);
    })
  }

  render() {
    return (
      <div>
        <div>
          <div className={ styles.graphLabMenu }>
            <div className={ styles.graphLabMenuCalculationModelItem }>
              <select ref="algorithm" className={ styles.graphLabMenuItemCalculationSelect } onChange={ this.changeAlgorithm }>
                <option value="" disabled selected>Select your model</option>
                { this.state.algorithms.map((d, idx) => {
                    return <option value={ d } key={ "evaluation" + d }>
                             { d }
                           </option>
                  }) }
              </select>
            </div>
            <div onClick={ this.showNodePropertyView } className={ styles.graphLabMenuItem }><img src="./../icon/graphlab_menu_icons/commonSetting.png" className={ styles.graphLabMenuIcon } data-tip="Setup Common Property for all nodes" /></div>
            <NodePropertyView label="All Nodes" ref="nodePropertyView" modelparameter={ this.state.modelparameter } />
            <Dropzone className={ styles.graphLabMenuItem } onDrop={ this.onDropAttributeImport } accept="text/csv">
              <div>
                <img src="./../icon/graphlab_menu_icons/importAttrs.png" className={ styles.graphLabMenuIcon } data-tip="Import Attribute Information from CSV file" />
              </div>
            </Dropzone>
            <Dropzone className={ styles.graphLabMenuItemTraining } onDrop={ this.onDropTraining } accept="text/csv">
              <div>
                <img src="./../icon/graphlab_menu_icons/training.jpg" className={ styles.graphLabMenuIcon } data-tip="Training Model. Drop File Here or Click" />
              </div>
            </Dropzone>
            <div className={ styles.graphLabMenuItemDropAnalyzingBox }>
              <Dropzone className={ styles.graphLabMenuItemDropAnalyzing } onDrop={ this.onDropAnalyzing } accept="text/csv" data-tip="Evaluate Model. Drop File Here Or Click">
                <div>
                  <img src="./../icon/graphlab_menu_icons/test.png" className={ styles.graphLabMenuIcon } />
                </div>
              </Dropzone>
              <div className={ styles.graphLabMenuItemAnalyzingTargetBox }>
                <select ref="evaluationMethod" className={ styles.graphLabMenuItemAnalyzingTarget }>
                  <option value="" disabled selected>Select Evaluation Method</option>
                  { this.state.evaluationMethod.map((d, idx) => {
                      return <option value={ d } key={ "evaluation" + d }>
                               { d }
                             </option>
                    }) }
                </select>
                <select ref="analyzingTarget" className={ styles.graphLabMenuItemAnalyzingTarget }>
                  <option value="" disabled selected>Select Target</option>
                  { this.state.analyzingTarget.map((d, idx) => {
                      return <option value={ d } key={ "option" + d }>
                               { d }
                             </option>
                    }) }
                </select>
              </div>
            </div>
            <div className={ styles.graphLabMenuItemGraphSearch }>
              <Dropzone className={ styles.graphLabMenuItemDropGraphSearch } onDrop={ this.onDropExplore } accept="text/csv">
                <div>
                  <img src="./../icon/graphlab_menu_icons/exploreGraph.png" className={ styles.graphLabMenuIcon } data-tip="Start Exploring Graph. Drop File Here or Click" />
                </div>
              </Dropzone>
              <div className={ styles.graphLabMenuItemStopSearch } onClick={ this.stopExploringGraph } data-tip="Stop Graph Exploration.">
                <img src="./../icon/graphlab_menu_icons/stopSearch.png" className={ styles.graphLabMenuIconStopSearchIcon } />
              </div>
            </div>
            <GraphSaveView saveCallBack={ this.saveCallBack } ref="graphSaveView" />
            <AddCustomNodeDialog showMessage={ this.showMessage } addCustomNode={ this.addCustomNodeToCanvas } ref="addCustomNodeToCanvas" />
            <div onClick={ this.save } className={ styles.graphLabMenuItemSave }><img src="./../icon/graphlab_menu_icons/save.png" className={ styles.graphLabMenuIcon } data-tip="Save Model" />
              <br/>
              <a className={ styles.graphLabMenuItemDownloadLink } href={ this.state.downloadContent } download="graph.json">
                { this.state.downloadLink }
              </a>
            </div>
            <div onClick={ this.openPreviousTestedGraph } className={ styles.graphLabMenuItem }><img src="./../icon/graphlab_menu_icons/testHistory.png" className={ styles.graphLabMenuIcon } data-tip="History of Testing Model" /></div>
            <div onClick={ this.showAddCustomNodeDialog } className={ styles.graphLabMenuItemLast }><img src="./../icon/graphlab_menu_icons/addNode.png" className={ styles.graphLabMenuIcon } data-tip="Add Node to Canvas" /></div>
          </div>
          <Graph ref="graph" items={ [] } modelparameter={ this.state.modelparameter } />
        </div>
        <Loading ref="loading" />
        <PopupMessage ref="popupMessage" />
        <PopupMessageSmallBox ref="popupMessageSmallBox" />
        <ModelHistoryDialogWithSearch ref="modelHistoryDialogWithSearch" setup={ this.setup } clear={ this.clear } />
        <ReactTooltip />
        <GraphTestResult ref="testResult" />
      </div>
    )
  }
}
