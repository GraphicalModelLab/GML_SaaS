import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Modal from 'react-modal';
import auth from "./auth";
import * as styles from './../../css/structure.css';
import Loading from './../loader/loading';

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    backgroundColor       : 'rgba(227, 224, 224, 0.9)',
    height                : '320px',
    width                 : '500px',
  },
  overlay : {
    backgroundImage       : 'url(../icon/LoginBack.jpg)'
  }
};

export default class RegistrationModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = { modalIsOpen: false };
        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.afterOpenModal = this.afterOpenModal.bind(this);
     }

    componentDidMount() {

    }

    openModal(userid) {

        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true},function(){
            let email = ReactDOM.findDOMNode(this.refs.email);
            email.focus();
        });


    }

    afterOpenModal() {

    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    handleSubmit(e){
        let email = ReactDOM.findDOMNode(this.refs.email).value.trim();
        let password = ReactDOM.findDOMNode(this.refs.password).value.trim();
        if (!email || !password) {
            return;
        }

        var parent=this;

        auth.register(email, password, this.props.type, (response) => {
            parent.refs.message.innerText = "A confirmation e-mail has been sent to "+email+". Please check the e-mail";
        })
      }


    render() {
        return <div>
                    <Modal
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <h2 className={styles.projectInfoModalTitle} ref="subtitle"> <span ref="titleText"></span> <div onClick={this.closeModal} className={styles.closeButton}><div><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></div></h2>

                        <div className={styles.loginBox}>
                        <div className={styles.loginTitle}>Graphical Model Lab</div>
                        <div><input type="text" placeholder="e-mail address" ref="email" className={styles.loginInput}/></div>
                        <div><input type="password" placeholder="password" ref="password" className={styles.loginInput}/></div>
                        <div onClick={this.handleSubmit.bind(this)} className={styles.loginButton}>Register</div>
                        <div className={styles.loginMessage} ref="message"></div>
                        </div>

                    </Modal>
                    <Loading ref="loading" />
               </div>
    }
}

RegistrationModal.contextTypes = {
            router: React.PropTypes.object
};