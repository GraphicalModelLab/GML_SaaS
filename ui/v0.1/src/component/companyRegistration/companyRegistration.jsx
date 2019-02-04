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
import * as styles from './../../css/accountManagement.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class CompanyRegistration extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.companyRegistration = this.companyRegistration.bind(this);

  }

  companyRegistration(e) {
    e.preventDefault();

    this.refs.loading.openModal();

    var data = {
      companyid: auth.getCompanyid(),
      userid: auth.getUserid(),
      token: auth.getToken(),
      companycode: this.refs.companycode.value,
      companyname: this.refs.companyname.value,
      code: 10
    };

    let parent = this;

    $.ajax({
      type: "post",
      url: "../commonModules/php/modules/Auth.php/auth/registerCompany",
      data: JSON.stringify(data),
      contentType: 'application/json',
      dataType: "json",
      success: function(json_data) {
        alert(JSON.stringify(json_data.body));
        if (json_data.body.registerationSuccessCode != -1) {
          parent.refs.message.innerText = "会社コードを登録しました。"

        //setTimeout(function(){
        // parent.refs.message.innerText = ""
        //}, 1000);
        } else {
          parent.refs.message.innerText = "指定された会社コードは既に存在しています。他のコードを入力してください"
        }

        parent.refs.loading.closeModal();
      },
      error: function(request, status, error) {
        alert(request.responseText);
      },
      complete: function() {}
    });

  }

  render() {
    return (
      <div>
        <div>
          <div className={ styles.accountRoleChangeTitle }> 会社登録 </div>
          <div className={ styles.accountRoleChangeItemBox }>
            <div className={ styles.accountRoleChangeItemName }>
              以下の会社情報を入力してください。
            </div>
            <input type="text" ref="companycode" placeholder="会社コード" />
            <input type="text" ref="companyname" placeholder="会社名" />
          </div>
          <div className={ styles.accountRoleChangeButton }>
            <div className={ styles.accountRoleChangeButtonBox }>
              <div onClick={ this.companyRegistration.bind(this) } width="200"><img src="../icon/mono_icons/exchange32.png" className={ styles.icon } />変更 <span ref="message"></span></div>
            </div>
          </div>
        </div>
        <Loading ref="loading" />
      </div>
    )
  }
}
