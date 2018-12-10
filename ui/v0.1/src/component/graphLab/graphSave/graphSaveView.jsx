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
import * as styles from './../../../css/structure.css';

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    height                : '250px',
    width                 : '300px'
  }
};

export default class GraphSaveView extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                message: "",
                properties: []
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.afterOpenModal = this.afterOpenModal.bind(this);
        this.save = this.save.bind(this);
   }

    openModal(message, modelname, modeltag, modeldescription) {
        var self = this;
        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true, message: message},function(){
            self.refs.modelName.focus();
            self.refs.modelName.value = modelname;
            self.refs.modelTag.value = modeltag;
            self.refs.modelDescription.value = modeldescription;
        });
    }

    afterOpenModal() {
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    save(){
        this.props.saveCallBack(this.refs.modelName.value, this.refs.modelTag.value, this.refs.modelDescription.value);

        this.closeModal();
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="Model Property"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.saveModelViewTitle}>
                            {this.state.message}
                            <h2 ref="subtitle"><div className={styles.modalTitle}>Model Information</div></h2> <div onClick={this.closeModal} className={styles.closeButtonGraphSaveView}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div>
                        </div>
                        <div className={styles.saveModelViewContent} ref="content">
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Model Name</div>
                                <input className={styles.savePropValue} ref="modelName" type="text" />
                            </div>

                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Model Tag</div>
                                <input className={styles.savePropValue} ref="modelTag" type="text" />
                            </div>
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Model Description</div>
                                <input className={styles.savePropValue} ref="modelDescription" type="text" />
                            </div>
                        </div>

                        <div onClick={this.save} className={styles.saveButtonBox}>
                            Save
                        </div>
                    </Modal>
              </div>
    }
}