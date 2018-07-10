import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class SocialConnect extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.accountRoleChangeTitle}> ソーシャルコネクトの接続 </div>


                    <img src="../icon/social_icons/facebook.jpg" className={styles.iconConnect}/>
                    <img src="../icon/social_icons/google.jpg" className={styles.iconConnect}/>
                    <img src="../icon/social_icons/twitter.png" className={styles.iconConnect}/>

                </div>
                 <Loading ref="loading"/>
            </div>
           )
    }
}