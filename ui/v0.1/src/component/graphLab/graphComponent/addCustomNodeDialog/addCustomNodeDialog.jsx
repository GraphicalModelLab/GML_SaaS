import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Modal from 'react-modal';
import auth from "./../../../auth/auth";
import $ from 'jquery';
import * as styles from './../../../../css/structure.css';

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


export default class AddCustomNodeDialog extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                plotTestHistory: false

        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.addCustomNode = this.addCustomNode.bind(this);
   }

    openModal(model_userid,modelid) {
        this.setState({modalIsOpen: true});
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    addCustomNode(){
        this.props.addCustomNode(this.refs.label.value, parseFloat(this.refs.x.value), parseFloat(this.refs.y.value));

        this.closeModal();
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="Model Property"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.nodePropertyViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}>Node Information</div><div onClick={this.closeModal} className={styles.closeButton}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></h2>
                        </div>

                        <div className={styles.saveModelViewContent} ref="content">
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Label</div>
                                <input className={styles.savePropValue} ref="label" type="text" />
                            </div>

                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>X</div>
                                <input className={styles.savePropValue} ref="x" type="text" />
                            </div>
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Y</div>
                                <input className={styles.savePropValue} ref="y" type="text" />
                            </div>
                        </div>

                        <div onClick={this.addCustomNode} className={styles.addCustomNodeButton}>
                            Add To Canvas
                        </div>
                    </Modal>
              </div>
    }
}