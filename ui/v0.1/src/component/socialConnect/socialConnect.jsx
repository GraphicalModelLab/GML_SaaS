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

      googleStatus: "none",
      googleRemainingTimeDay: "",
      googleRemainingTimeHour: "",
      googleRemainingTimeMinute: "",
      googleRemainingTimeSecond: "",

      twitterStatus: "unconnected"
    };

    this.goToFacebookAppsLogin = this.goToFacebookAppsLogin.bind(this);
    this.goToGoogleAppsLogin = this.goToGoogleAppsLogin.bind(this);
    this.disconnectFacebook = this.disconnectFacebook.bind(this);
  }

  componentDidMount() {
    var self = this;

    var data = {
      companyid: auth.getCompanyid(),
      userid: auth.getUserid(),
      token: auth.getToken(),
      code: 10
    };

    $.ajax({
      type: "post",
      url: "../commonModules/php/modules/Auth.php/auth/social/connect/status",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      success: function(response) {

        console.log(response);
        if (response.body.code == 401) {
          auth.logout();
        }

        var facebookStatus = "unconnected";
        var facebookRemainingTimeDay = "";
        var facebookRemainingTimeHour = "";
        var facebookRemainingTimeMinute = "";
        var facebookRemainingTimeSecond = "";

        if (response.body.connectedWithFacebook) {
          facebookStatus = "active";
          var timeToRemain = response.body.facebookExpiresIn - response.body.facebookTimePassed;

          var day = timeToRemain / (60 * 60 * 24) | 0;
          var hour = (timeToRemain - 60 * 60 * 24 * day) / (60 * 60) | 0;

          var minute = (timeToRemain - 60 * 60 * 24 * day - hour * 60 * 60) / 60 | 0;
          var second = (timeToRemain - 60 * 60 * 24 * day - hour * 60 * 60 - minute * 60);

          facebookRemainingTimeDay = day;
          facebookRemainingTimeHour = hour;
          facebookRemainingTimeMinute = minute;
          facebookRemainingTimeSecond = second;

        } else {
          if (response.body.facebookExpired) {
            facebookStatus = "expired";
          } else {
            facebookStatus = "unconnected";
          }
        }

        var googleStatus = "unconnected";
        var googleRemainingTimeDay = "";
        var googleRemainingTimeHour = "";
        var googleRemainingTimeMinute = "";
        var googleRemainingTimeSecond = "";

        if (response.body.connectedWithGoogle) {
          googleStatus = "active";
          var timeToRemain = response.body.googleExpiresIn - response.body.googleTimePassed;

          var day = timeToRemain / (60 * 60 * 24) | 0;
          var hour = (timeToRemain - 60 * 60 * 24 * day) / (60 * 60) | 0;

          var minute = (timeToRemain - 60 * 60 * 24 * day - hour * 60 * 60) / 60 | 0;
          var second = (timeToRemain - 60 * 60 * 24 * day - hour * 60 * 60 - minute * 60);

          googleRemainingTimeDay = day;
          googleRemainingTimeHour = hour;
          googleRemainingTimeMinute = minute;
          googleRemainingTimeSecond = second;

        } else {
          if (response.body.googleExpired) {
            googleStatus = "expired";
          } else {
            googleStatus = "unconnected";
          }
        }

        self.setState({
          facebookStatus: facebookStatus,
          facebookRemainingTimeDay: facebookRemainingTimeDay,
          facebookRemainingTimeHour: facebookRemainingTimeHour,
          facebookRemainingTimeMinute: facebookRemainingTimeMinute,
          facebookRemainingTimeSecond: facebookRemainingTimeSecond,

          googleStatus: googleStatus,
          googleRemainingTimeDay: googleRemainingTimeDay,
          googleRemainingTimeHour: googleRemainingTimeHour,
          googleRemainingTimeMinute: googleRemainingTimeMinute,
          googleRemainingTimeSecond: googleRemainingTimeSecond
        });
      },
      error: function(request, status, error) {
        alert("Failed to get social connection status");
        alert(request.responseText);
      },
      complete: function() {}
    });
  }

  goToGoogleAppsLogin(e) {
    e.preventDefault();

    window.location.href = '../commonModules/php/modules/Auth.php/auth/googleAppsLogin/connect?companyid=' + auth.getCompanyid() + '&userid=' + auth.getUserid() + '&token=' + auth.getToken();
  }

  goToFacebookAppsLogin(e) {
    e.preventDefault();

    window.location.href = '../commonModules/php/modules/Auth.php/auth/facebookAppsLogin/connect?companyid=' + auth.getCompanyid() + '&userid=' + auth.getUserid() + '&token=' + auth.getToken();
  }

  disconnectFacebook() {
    var self = this;

    var data = {
      companyid: auth.getCompanyid(),
      userid: auth.getUserid(),
      token: auth.getToken(),
      code: 10
    };

    $.ajax({
      type: "post",
      url: "../commonModules/php/modules/Auth.php/auth/social/connect/facebook/disconnect",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      success: function(response) {
        console.log(response);
        if (response.body.code == 401) {
          auth.logout();
        }

        self.setState({
          facebookStatus: "expired",
        });
      },
      error: function(request, status, error) {
        alert("Failed to get social connection status");
        alert(request.responseText);
      },
      complete: function() {}
    });
  }

  render() {
    return (
      <div>
        <div>
          <div className={ styles.accountRoleChangeTitle }> Connect to Social Data Source </div>
          <div className={ styles.socialConnectionBody }>
            <div className={ styles.socialConnectionRecord }>
              <img onClick={ this.disconnectFacebook } src="./../icon/mono_icons/minus32.png" className={ styles.socialConnectionDisconnectIcon } />
              <img onClick={ this.goToFacebookAppsLogin } src="../icon/social_icons/facebook.jpg" className={ styles.socialConnectionIcon } />
              { (() => {
                  switch (this.state.facebookStatus) {
                    case "active":
                      return <span><span className={ styles.socialConnectionRecordStatusActive }>
                                                                                                                                                                                                                                                       Active
                                                                                                                                                                                                                                                      </span>
                             <span className={ styles.socialConnectionRecordStatusRemainingTime }>
                                                                                                                                                                                                                                                           Expire in<br/>
                                                                                                                                                                                                                                                           { this.state.facebookRemainingTimeDay } Day<br/>
                                                                                                                                                                                                                                                           { this.state.facebookRemainingTimeHour } Hour<br/>
                                                                                                                                                                                                                                                           { this.state.facebookRemainingTimeMinute } Minute<br/>
                                                                                                                                                                                                                                                           { this.state.facebookRemainingTimeSecond } Seconds
                                                                                                                                                                                                                                                      </span>
                             </span>
                    case "expired":
                      return <span className={ styles.socialConnectionRecordStatusExpired }>
                                                                                                                                                                                                                                                       Expired
                                                                                                                                                                                                                                                      </span>
                
                    case "unconnected":
                      return <span className={ styles.socialConnectionRecordStatusNotConnected }>
                                                                                                                                                                                                                                                       Unconnected
                                                                                                                                                                                                                                                      </span>
                    case "none":
                      return <span className={ styles.socialConnectionRecordStatusNotConnected }>
                                                                                                                                                                                                                                                      </span>
                
                  }
                })() }
            </div>
          </div>
          <div className={ styles.socialConnectionBody }>
            <div className={ styles.socialConnectionRecord }>
              <img src="./../icon/mono_icons/minus32.png" className={ styles.socialConnectionDisconnectIcon } />
              <img onClick={ this.goToGoogleAppsLogin } src="../icon/social_icons/google.jpg" className={ styles.socialConnectionIcon } />
              { (() => {
                  switch (this.state.googleStatus) {
                    case "active":
                      return <span><span className={ styles.socialConnectionRecordStatusActive }>
                                                                                                                                                                                                                                                       Active
                                                                                                                                                                                                                                                      </span>
                             <span className={ styles.socialConnectionRecordStatusRemainingTime }>
                                                                                                                                                                                                                                                           Expire in<br/>
                                                                                                                                                                                                                                                           { this.state.googleRemainingTimeDay } Day<br/>
                                                                                                                                                                                                                                                           { this.state.googleRemainingTimeHour } Hour<br/>
                                                                                                                                                                                                                                                           { this.state.googleRemainingTimeMinute } Minute<br/>
                                                                                                                                                                                                                                                           { this.state.googleRemainingTimeSecond } Seconds
                                                                                                                                                                                                                                                      </span>
                             </span>
                
                    case "expired":
                      return <span className={ styles.socialConnectionRecordStatusExpired }>
                                                                                                                                                                                                                                                       Expired
                                                                                                                                                                                                                                                      </span>
                
                    case "unconnected":
                      return <span className={ styles.socialConnectionRecordStatusNotConnected }>
                                                                                                                                                                                                                                                       Unconnected
                                                                                                                                                                                                                                                      </span>
                  }
                })() }
            </div>
          </div>
          {/*
          <div className={ styles.socialConnectionBody }>
            <div className={ styles.socialConnectionRecord }>
              <img src="./../icon/mono_icons/minus32.png" className={ styles.socialConnectionDisconnectIcon } />
              <img src="../icon/social_icons/twitter.png" className={ styles.socialConnectionIcon } />
              { (() => {
                  switch (this.state.twitterStatus) {
                    case "active":
                      return <span className={ styles.socialConnectionRecordStatusActive }>
                                                                                                                                                                                                                                                       Active
                                                                                                                                                                                                                                                      </span>
                    case "expired":
                      return <span className={ styles.socialConnectionRecordStatusExpired }>
                                                                                                                                                                                                                                                       Expired
                                                                                                                                                                                                                                                      </span>

                    case "unconnected":
                      return <span className={ styles.socialConnectionRecordStatusNotConnected }>
                                                                                                                                                                                                                                                       Unconnected
                                                                                                                                                                                                                                                      </span>
                  }
                })() }
            </div>
          </div>
          */}
        </div>
        <Loading ref="loading" />
      </div>
    )
  }
}
