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
import { SketchPicker } from 'react-color';
import color from "./color";

const customStyles = {
  content: {
    top: '50%',
    left: '50%',
    right: 'auto',
    bottom: 'auto',
    marginRight: '-50%',
    transform: 'translate(-50%, -50%)',
    height: '420px',
    width: '220px'
  }
};

export default class GraphColorView extends React.Component<Props, {}> {

  constructor(props) {
    super(props);
    this.state = {
      modalIsOpen: false
    };

    this.openModal = this.openModal.bind(this);
    this.closeModal = this.closeModal.bind(this);
    this.afterOpenModal = this.afterOpenModal.bind(this);
    this.save = this.save.bind(this);
  }

  openModal() {
    this.setState({
      modalIsOpen: true
    });
  }

  afterOpenModal() {}

  closeModal() {
    this.setState({
      modalIsOpen: false
    });
  }

  save() {
    this.props.saveCallBack(this.refs.colorPanel.state.hex);

    this.closeModal();
  }

  render() {
    return <div>
             <Modal contentLabel="Graph Color" isOpen={ this.state.modalIsOpen } onAfterOpen={ this.afterOpenModal } style={ customStyles } ref="modal">
               <div className={ styles.saveGraphColorViewTitle }>
                 <h2 ref="subtitle"><div className={ styles.modalGraphColorViewTitle }>Graph Color</div></h2>
                 <div onClick={ this.closeModal } className={ styles.closeButtonGraphSaveView }><img src="../icon/mono_icons/stop32.png" className={ styles.icon } /></div>
               </div>
               <div className={ styles.graphColorViewContent } ref="content">
                 <SketchPicker ref="colorPanel" color={ color.get() } />
               </div>
               <div onClick={ this.save } className={ styles.saveGraphColorViewButtonBox }>
                 Save
               </div>
             </Modal>
           </div>
  }
}
