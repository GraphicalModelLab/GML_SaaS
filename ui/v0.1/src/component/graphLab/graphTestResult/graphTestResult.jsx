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

    afterOpenModal() {
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="Model Property"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.saveModelViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}>Test Result</div></h2> <div onClick={this.closeModal} className={styles.closeButtonGraphSaveView}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div>
                        </div>
                        <div className={styles.saveModelViewContent} ref="content">
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Accuracy</div>
                                <div className={styles.savePropValue}>{this.state.accuracy}</div>
                            </div>

                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>Evaluation Method </div>
                                <div className={styles.savePropValue}>{this.state.evaluation_method}</div>
                            </div>
                        </div>
                    </Modal>
              </div>
    }
}