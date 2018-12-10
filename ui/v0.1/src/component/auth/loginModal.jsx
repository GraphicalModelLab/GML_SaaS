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
    backgroundColor       : 'rgba(255, 255, 255, 0.8)',
    height                : '500px',
    width                 : '500px',

  },
  overlay : {
    backgroundImage       : 'url(../icon/LoginBack.jpg)'
  }
};

export default class LoginModal extends React.Component {
    constructor(props) {
        super(props);
        this.state = { modalIsOpen: false };
        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.afterOpenModal = this.afterOpenModal.bind(this);
        this.goToRegistration = this.goToRegistration.bind(this);

        this.goToGoogleAppsLogin = this.goToGoogleAppsLogin.bind(this);
        this.goToFacebookAppsLogin = this.goToFacebookAppsLogin.bind(this);
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
        e.preventDefault();

        let email = ReactDOM.findDOMNode(this.refs.email).value.trim();
        let password = ReactDOM.findDOMNode(this.refs.password).value.trim();
        if (!email || !password) {
            return;
        }

        var parent=this;

        auth.login(email, password, (response) => {
            if(response.code == 900){
                parent.refs.message.innerText = email+" has not been registered yet. Plrease register your account at first.";
            }else if(response.code == 901){
                parent.refs.message.innerText = email+" has not completed being registered yet. When you tried registering, you should have had an confirmation e-mail from us. If the e-mail has not been sent to you, please try registering from the beginning again.";
            }else if(response.code == 902){
                parent.refs.message.innerText = "The give password is different from the registered password. Please give the correct password";
            }else if(response.code == -1){
                parent.refs.message.innerText = "Unfortunately, the authentication system has not been up/running. Please contact the system administrator.";
            }

            if(parent.refs.loading){
              parent.refs.loading.closeModal();
            }

            if (!response.authenticated) return;

            if (parent.state && parent.state.nextPathname) {
              parent.context.router.push(parent.state.nextPathname)
            } else {
              parent.context.router.push('/top')
            }
        })


        this.refs.loading.openModal();
    }

    goToRegistration(){
      this.context.router.push("/register")
    }

    goToGoogleAppsLogin(e){
        e.preventDefault();
        window.location.href = '../commonModules/php/modules/Auth.php/auth/googleAppsLogin/login?companyid='+auth.getCompanyid();
    }

    goToFacebookAppsLogin(e){
        e.preventDefault();

        window.location.href = '../commonModules/php/modules/Auth.php/auth/facebookAppsLogin/login?companyid='+auth.getCompanyid();
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="login"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.loginBox}>
                        <img className={styles.loginLogo} src='../icon/infographic/ccs_logo.png'/>

                        <div><input type="text" placeholder="e-mail address" ref="email" className={styles.loginInput}/></div>
                        <div><input type="password" placeholder="password" ref="password" className={styles.loginInput}/></div>
                        <div onClick={this.handleSubmit.bind(this)} className={styles.loginButton}>Sign in</div>

                        <div className={styles.loginRegistration}>If you are not a member, <span onClick={this.goToRegistration.bind(this)} className={styles.loginRegistrationTitle} >Register from here</span></div>

                        <div onClick={this.goToGoogleAppsLogin} className={styles.loginFederationBox}>
                            <img className={styles.loginFederationGoogle} src='../icon/openId_icons/googleapps/btn_google_signin_dark_pressed_web@2x.png'/>
                        </div>
                        <div onClick={this.goToFacebookAppsLogin} className={styles.loginFederationBox}>
                            <img className={styles.loginFederationFacebook} src='../icon/openId_icons/facebookapps/SignUpFB.png'/>
                        </div>
                        <div className={styles.loginMessage} ref="message"></div>
                        </div>

                    </Modal>
                    <Loading ref="loading" />
               </div>
    }
}

LoginModal.contextTypes = {
    router: React.PropTypes.object
};