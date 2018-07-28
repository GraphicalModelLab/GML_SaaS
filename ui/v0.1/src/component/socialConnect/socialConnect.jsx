import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class SocialConnect extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.state = {
            facebookStatus: "none",

            facebookRemainingTimeDay: "",
            facebookRemainingTimeHour: "",
            facebookRemainingTimeMinute: "",
            facebookRemainingTimeSecond: "",

            connectedDateWithFacebook: "",
            facebookTimePassed: "",

            googleStatus: "unconnected",
            twitterStatus: "unconnected"
        };

        this.goToFacebookAppsLogin = this.goToFacebookAppsLogin.bind(this);
        this.disconnectFacebook = this.disconnectFacebook.bind(this);
    }

    componentDidMount() {
        var self = this;

        var data = {
            companyid: auth.getCompanyid(),
            userid:auth.getUserid(),
            token: auth.getToken(),
            code:10
        };

        $.ajax({
                type:"post",
                url:"../commonModules/php/modules/Auth.php/auth/social/connect/status",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {

                   console.log(json_data);
                   var facebookStatus = "unconnected";
                   var facebookRemainingTimeDay = "";
                   var facebookRemainingTimeHour = "";
                   var facebookRemainingTimeMinute = "";
                   var facebookRemainingTimeSecond = "";

                   if(json_data.body.connectedWithFacebook){
                     facebookStatus = "active";
                     var timeToRemain = json_data.body.facebookExpiresIn - json_data.body.facebookTimePassed;

                     var day = timeToRemain/(60*60*24) | 0;
                     var hour = (timeToRemain - 60*60*24*day)/(60*60) | 0;

                     var minute = (timeToRemain - 60*60*24*day - hour * 60 * 60)/60 | 0;
                     var second = (timeToRemain - 60*60*24*day - hour * 60 * 60 - minute * 60);

                     facebookRemainingTimeDay = day;
                     facebookRemainingTimeHour = hour;
                     facebookRemainingTimeMinute = minute;
                     facebookRemainingTimeSecond = second;

                   }else{
                     if(json_data.body.facebookExpiresIn > 0){
                        facebookStatus = "expired";
                     }
                   }

                   self.setState({
                        facebookStatus: facebookStatus,
                        facebookRemainingTimeDay: facebookRemainingTimeDay,
                        facebookRemainingTimeHour: facebookRemainingTimeHour,
                        facebookRemainingTimeMinute: facebookRemainingTimeMinute,
                        facebookRemainingTimeSecond: facebookRemainingTimeSecond,
                   });
                },
                error: function (request, status, error) {
                    alert("Failed to get social connection status");
                    alert(request.responseText);
                },
                complete: function() {
                }
        });
    }

    goToFacebookAppsLogin(e){
        e.preventDefault();

        window.location.href = '../commonModules/php/modules/Auth.php/auth/facebookAppsLogin/connect?companyid='+auth.getCompanyid()+'&userid='+auth.getUserid()+'&token='+auth.getToken();
    }

    disconnectFacebook(){
        var self = this;

        var data = {
            companyid: auth.getCompanyid(),
            userid:auth.getUserid(),
            token: auth.getToken(),
            code:10
        };

        $.ajax({
                type:"post",
                url:"../commonModules/php/modules/Auth.php/auth/social/connect/facebook/disconnect",
                data:JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(json_data) {
                    console.log(json_data);

                    self.setState({
                        facebookStatus: "expired",
                    });
                },
                error: function (request, status, error) {
                    alert("Failed to get social connection status");
                    alert(request.responseText);
                },
                complete: function() {
                }
        });
    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.accountRoleChangeTitle}> Connect to Social Data Source </div>

                    <div className={styles.socialConnectionBody}>
                        <div className={styles.socialConnectionRecord}>
                            <img onClick={this.disconnectFacebook} src="./../icon/mono_icons/minus32.png" className={styles.socialConnectionDisconnectIcon}/>
                            <img onClick={this.goToFacebookAppsLogin} src="../icon/social_icons/facebook.jpg" className={styles.socialConnectionIcon}/>
                            {(() => {
                                switch (this.state.facebookStatus) {
                                    case "active":
                                        return <span><span className={styles.socialConnectionRecordStatusActive}>
                                                Active
                                               </span>
                                               <span className={styles.socialConnectionRecordStatusRemainingTime}>
                                                    Expire in<br/>
                                                    {this.state.facebookRemainingTimeDay} Day<br/>
                                                    {this.state.facebookRemainingTimeHour} Hour<br/>
                                                    {this.state.facebookRemainingTimeMinute} Minute<br/>
                                                    {this.state.facebookRemainingTimeSecond} Seconds
                                               </span>
                                               </span>
                                    case "expired":
                                        return <span className={styles.socialConnectionRecordStatusExpired}>
                                                Expired
                                               </span>

                                    case "unconnected":
                                        return <span className={styles.socialConnectionRecordStatusNotConnected}>
                                                Unconnected
                                               </span>
                                    case "none":
                                        return <span className={styles.socialConnectionRecordStatusNotConnected}>

                                               </span>

                                }
                            })()}
                        </div>
                    </div>
                    <div className={styles.socialConnectionBody}>
                        <div className={styles.socialConnectionRecord}>
                            <img src="./../icon/mono_icons/minus32.png" className={styles.socialConnectionDisconnectIcon}/>
                            <img src="../icon/social_icons/google.jpg" className={styles.socialConnectionIcon}/>
                            {(() => {
                                switch (this.state.googleStatus) {
                                    case "active":
                                        return <span className={styles.socialConnectionRecordStatusActive}>
                                                Active
                                               </span>


                                    case "expired":
                                        return <span className={styles.socialConnectionRecordStatusExpired}>
                                                Expired
                                               </span>

                                    case "unconnected":
                                        return <span className={styles.socialConnectionRecordStatusNotConnected}>
                                                Unconnected
                                               </span>
                                }
                            })()}


                        </div>
                    </div>
                    <div className={styles.socialConnectionBody}>
                        <div className={styles.socialConnectionRecord}>
                            <img src="./../icon/mono_icons/minus32.png" className={styles.socialConnectionDisconnectIcon}/>
                            <img src="../icon/social_icons/twitter.png" className={styles.socialConnectionIcon}/>
                            {(() => {
                                switch (this.state.twitterStatus) {
                                    case "active":
                                        return <span className={styles.socialConnectionRecordStatusActive}>
                                                Active
                                               </span>
                                    case "expired":
                                        return <span className={styles.socialConnectionRecordStatusExpired}>
                                                Expired
                                               </span>

                                    case "unconnected":
                                        return <span className={styles.socialConnectionRecordStatusNotConnected}>
                                                Unconnected
                                               </span>
                                }
                            })()}
                        </div>
                    </div>

                </div>
                 <Loading ref="loading"/>
            </div>
           )
    }
}