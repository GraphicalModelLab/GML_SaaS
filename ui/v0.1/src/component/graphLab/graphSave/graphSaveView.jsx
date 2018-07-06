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
    height                : '400px',
    width                 : '280px;'
  }
};

export default class GraphSaveView extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                properties: []
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.afterOpenModal = this.afterOpenModal.bind(this);
        this.save = this.save.bind(this);
   }

    openModal() {
        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true});
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

                        <div className={styles.nodePropertyViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}>モデル　プロパティ</div><div onClick={this.closeModal} className={styles.closeButton}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></h2>
                        </div>
                        <div className={styles.nodePropertyViewContent} ref="content">
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>モデル名</div>
                                <input className={styles.savePropValue} ref="modelName" type="text" />
                            </div>
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>タグ</div>
                                <input className={styles.savePropValue} ref="modelTag" type="text" />
                            </div>
                            <div className={styles.saveProp}>
                                <div className={styles.savePropName}>詳細</div>
                                <input className={styles.savePropValue} ref="modelDescription" type="text" />
                            </div>
                        </div>

                        <div onClick={this.save} className={styles.saveButtonBox}>
                            保存
                        </div>
                    </Modal>
              </div>
    }
}