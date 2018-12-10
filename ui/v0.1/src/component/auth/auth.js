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

import $ from 'jquery';

module.exports = {
  login(email, pass, cb) {
    cb = arguments[arguments.length - 1]
    if (localStorage.token) {
      if (cb) cb(true)
      this.onChange(true)
      return
    }

    loginRequest(email, pass, (res) => {
      if (res.authenticated) {

        localStorage.token = res.token
        localStorage.userid = res.userid
        role = res.role;

        this.onChange(true)
      } else {
        this.onChange(false)
      }

      if(cb) cb(res)
    })
  },

  register(email, pass, type, cb){
    this.logout();
    cb = arguments[arguments.length - 1]
    if (localStorage.token) {
      if (cb) cb(true)
      this.onChange(true)
      return
    }
    registerRequest(email, pass, type, (res) => {
            cb(res);
    })
  },

  getToken() {
    return localStorage.token
  },

  getUserid() {
    return localStorage.userid
  },

  getRole(){
    return role;
  },

  getCompanyid(){
    // company id should have been setup before hand, i.e. in index.php
    return companyid;
  },

  logout(cb) {
    delete localStorage.token
    if (cb) cb()
    this.onChange(false)
  },

  loggedIn() {
    return !!localStorage.token
  },

  onChange() {}
  ,
  checkIfRedirectToLogin(code){
    if(code == 10000){
        return true;
    }
    return false;
  }
}

function loginRequest(email, pass, cb) {
  setTimeout(() => {

    var data = {
        companyid:companyid,
        email:email ? email : "",
        password:pass ? pass : ""
    };

    $.ajax({
            type:"post",
            url:"../commonModules/php/modules/Auth.php/auth/login",
            data:JSON.stringify(data),
            contentType: 'application/json',
            dataType: "json",
            success: function(json_data) {
                if(json_data.body && !(json_data.body.code == 900 || json_data.body.code == 901 || json_data.body.code == 902)){
                    cb({
                        authenticated: true,
                        token: json_data.body.token,
                        userid: email,
                        role: json_data.body.role,
                        code: json_data.body.code
                    })
                }else if(json_data.body){
                    cb({
                        authenticated: false,
                        code: json_data.body.code
                    })
                }else{
                    cb({authenticated: false, code: -1})
                }
            },
            error: function (request, status, error) {
                cb({ authenticated: false})
                alert("error");
                alert(request.responseText);
            },
            complete: function() {
            }
    });

  }, 0)
}


function registerRequest(email, pass, type, cb) {
     setTimeout(() => {

     var data = {
             companyid:companyid,
             email:email ? email : "",
             password:pass ? pass : ""
     };

     $.ajax({
        type:"post",
        url:"../commonModules/php/modules/Auth.php/auth/register",
        data:JSON.stringify(data),
        contentType: 'application/json',
        dataType: "json",
        success: function(validationCodeData) {
                var codeValue = validationCodeData.body.validationCode;
                var data = {
                    mailto: email ? email : "",
                    subject: "Graphical Model Lab - Confirm Registration",
                    codeValue: codeValue,
                    email: email,
                    companyid : companyid,
                    type: type
                };

                $.ajax({
                        type:"post",
                        url:"../commonModules/php/modules/MailApp.php/sendRegistrationMailBySendGrid",
                        data:JSON.stringify(data),
                        contentType: 'application/json',
                        dataType: "json",
                        success: function(json_data) {
                                cb(json_data);
                        },
                        error: function (request, status, error) {
                            cb({ authenticated: false, code: json_data.code })

                        },
                        complete: function() {
                        }
                });
        },
        error: function (request, status, error) {

                    cb({ authenticated: false })
                alert("error");
                alert(request.responseText);
        },
        complete: function() {
        }
    });

  }, 0)
}