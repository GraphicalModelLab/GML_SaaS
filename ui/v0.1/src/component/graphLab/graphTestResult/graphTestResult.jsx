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
import * as styles from './../../../css/graphLab.css';

const customStyles = {
  content: {
    top: '50%',
    left: '50%',
    right: 'auto',
    bottom: 'auto',
    marginRight: '-50%',
    transform: 'translate(-50%, -50%)',
    height: '250px',
    width: '300px'
  }
};

export default class GraphTestResult extends React.Component<Props, {}> {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false,
      accuracy: "",
      evaluation_method: ""
    };

    this.openModal = this.openModal.bind(this);
    this.closeModal = this.closeModal.bind(this);
    this.afterOpenModal = this.afterOpenModal.bind(this);
  }

  openModal(accuracy, evaluation_method) {
    // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
    this.setState({
      modalIsOpen: true,
      accuracy: accuracy,
      evaluation_method: evaluation_method
    });
  }

  afterOpenModal() {}

  closeModal() {
    this.setState({
      modalIsOpen: false
    });
  }

  render() {
    return <div>
             <Modal contentLabel="Model Property" isOpen={ this.state.modalIsOpen } onAfterOpen={ this.afterOpenModal } style={ customStyles } ref="modal">
               <div className={ styles.modalTitleBox }>
                 <h2 ref="subtitle"><div className={ styles.modalTitle }>Test Result</div></h2>
                 <div onClick={ this.closeModal } className={ styles.closeButtonGraphSaveView }><img src="../icon/mono_icons/stop32.png" className={ styles.icon } /></div>
               </div>
               <div className={ styles.testResultlViewContent } ref="content">
                 <div className={ styles.testResultProp }>
                   <div className={ styles.testResultPropName }>Accuracy</div>
                   <div className={ styles.testResultPropValue }>
                     { this.state.accuracy }
                   </div>
                 </div>
                 <div className={ styles.testResultProp }>
                   <div className={ styles.testResultPropName }>Evaluation Method </div>
                   <div className={ styles.testResultPropValue }>
                     { this.state.evaluation_method }
                   </div>
                 </div>
               </div>
             </Modal>
           </div>
  }
}
