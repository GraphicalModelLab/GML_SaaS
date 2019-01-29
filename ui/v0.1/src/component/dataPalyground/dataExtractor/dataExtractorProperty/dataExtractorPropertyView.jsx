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
import Modal from 'react-modal';
import * as styles from './../../../../css/structure.css';
import DataExtractorProperty from './dataExtractorProperty';
import auth from "./../../../auth/auth";
import $ from 'jquery';

const customStyles = {
  content: {
    top: '50%',
    left: '50%',
    right: 'auto',
    bottom: 'auto',
    marginRight: '-50%',
    transform: 'translate(-50%, -50%)',
    height: '400px',
    width: '300px'
  }
};

export default class DataExtractorPropertyView extends React.Component<Props, {}> {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      properties: [],
      count: 0
    };

    this.openModal = this.openModal.bind(this);
    this.closeModal = this.closeModal.bind(this);
    this.syncProperty = this.syncProperty.bind(this);
    this.afterOpenModal = this.afterOpenModal.bind(this);
    this.addProperty = this.addProperty.bind(this);
    this.getProperties = this.getProperties.bind(this);
    this.addProperties = this.addProperties.bind(this);
    this.deleteCallBack = this.deleteCallBack.bind(this);
    this.executeExtractor = this.executeExtractor.bind(this);

  }

  openModal() {
    // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
    this.setState({
      modalIsOpen: true
    });
  }

  afterOpenModal() {}

  closeModal() {
    this.syncProperty();

    this.setState({
      modalIsOpen: false
    });
  }

  syncProperty() {
    this.state.properties.map((d, idx) => {
      this.state.properties[idx].name = this.refs["prop" + idx].state.propAutoCompletedValue;
      this.state.properties[idx].value = this.refs["prop" + idx].refs.propValue.value;
    });
  }

  addProperty() {
    this.state.properties.push({
      name: "",
      value: "",
      key: this.state.count
    });

    this.setState({
      properties: this.state.properties,
      count: this.state.count + 1
    });
  }

  getProperties() {
    return this.state.properties;
  }

  addProperties(properties) {
    this.setState({
      properties: properties
    });
  }

  deleteCallBack(removedKey) {
    this.syncProperty();

    var newProps = [];
    for (let index in this.state.properties) {
      if (this.state.properties[index].key != removedKey) {
        newProps.push(this.state.properties[index]);
      }
    }

    var self = this;
    this.setState({
      properties: []
    }, function() {
      self.setState({
        properties: newProps
      });
    });

  }

  executeExtractor() {
    this.syncProperty();
    var data = {
      code: 10,
      userid: auth.getUserid(),
      companyid: auth.getCompanyid(),
      extractorId: this.props.extractorId,
      extractorParamValues: this.state.properties
    };

    var self = this;
    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/data/extractor",
      type: "post",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        console.log("success for save");
        console.log(response);
        if (response.body.code == 401) {
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
  }

  render() {
    return <div>
             <Modal contentLabel="Property View" isOpen={ this.state.modalIsOpen } onAfterOpen={ this.afterOpenModal } style={ customStyles } ref="modal">
               <div className={ styles.nodePropertyViewTitle }>
                 <h2 ref="subtitle"><div className={ styles.modalTitle }>{ this.props.label } </div></h2>
                 <div onClick={ this.closeModal } className={ styles.closeButton }><img src="../icon/mono_icons/stop32.png" className={ styles.icon } /></div>
               </div>
               <div className={ styles.nodePropertyViewContent } ref="content">
                 <div className={ styles.nodeProp }>
                   <span className={ styles.nodePropAdd } onClick={ this.addProperty }><img src="./../icon/mono_icons/plus32.png" className={ styles.icon }/></span>
                   <span className={ styles.nodePropNameHeader }>property</span>
                   <span className={ styles.nodePropValueHeader }>value</span>
                 </div>
                 { this.state.properties.map((d, idx) => {
                     return <DataExtractorProperty key={ "nodeprop" + idx } ref={ "prop" + idx } deleteCallBack={ this.deleteCallBack } indexKey={ d.key } name={ d.name }
                              value={ d.value } modelparameter={ this.props.modelparameter } />
                   }) }
               </div>
               <div onClick={ this.executeExtractor } className={ styles.saveButtonBox }>
                 Execute
               </div>
             </Modal>
           </div>
  }
}
