import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Modal from 'react-modal';
import * as styles from './../../../css/structure.css';
import NodeProperty from './nodeProperty';

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    height                : '400px',
    width                 : '300px'
  }
};

export default class NodePropertyView extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                properties: []
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.afterOpenModal = this.afterOpenModal.bind(this);
        this.addProperty = this.addProperty.bind(this);
        this.getProperties = this.getProperties.bind(this);
        this.addProperties = this.addProperties.bind(this);
   }

    openModal() {
        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true});
    }

    afterOpenModal() {
    }

    closeModal() {

        this.state.properties.map((d, idx) => {
            this.state.properties[idx].name = this.refs["prop"+idx].refs.propName.value;
            this.state.properties[idx].value = this.refs["prop"+idx].refs.propValue.value;
        })

        this.setState({modalIsOpen: false});
    }

    addProperty(){
        this.state.properties.push({name:"", value:""});

        this.setState({properties: this.state.properties});
    }

    getProperties(){
             return this.state.properties;
    }

    addProperties(properties){
        this.setState({properties: properties});
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="Property View"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.nodePropertyViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}>Property - {this.props.label} </div><div onClick={this.closeModal} className={styles.closeButton}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></h2>
                        </div>
                        <div className={styles.nodePropertyViewContent} ref="content">
                            <div onClick={this.addProperty}　className={styles.nodePropertyViewFooter}>
                                プロパティ追加<img src="./../icon/mono_icons/plus32.png" className={styles.icon}/>
                            </div>
                            <div className={styles.nodeProp}>
                                <span className={styles.nodePropNameHeader}>property</span>
                                <span className={styles.nodePropValueHeader}>value</span>
                            </div>
                            { this.state.properties.map((d, idx) => {
                                return <NodeProperty key={"nodeprop"+idx} ref={"prop"+idx} name={d.name} value={d.value}/>
                            }) }
                        </div>
                    </Modal>
              </div>
    }
}