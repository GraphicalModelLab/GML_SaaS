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
import * as styles from './../../../css/dataPlayground.css';
import auth from "./../../auth/auth";
import Dropzone from 'react-dropzone';
import $ from 'jquery';
import Loading from './../../loader/loading';
import PopupMessage from './../../popupMessage/popupMessage';

export default class HtmlConverter extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      converterEngines: []
    };

    this.execute = this.execute.bind(this);
  }

  componentDidMount() {
      var self = this;

      $.ajax({
            url: "../commonModules/php/modules/GML.php/gml/data/htmlconverterengine/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
            type: "get",
            headers: {
              Authorization: "Bearer " + auth.getToken()
            },
            success: function(response) {
              console.log("converter list");
              console.log(response);
              if (response.body.code == 401) {
                auth.logout();
              }

              self.setState({
                converterEngines: response.body.converterEngineIds
              });
            },
            error: function(request, status, error) {
              alert("error");
              console.log(request.responseText);
              console.log(status);
              console.log(error);
            }
      });
  }

  execute(){
    var self = this;
    self.refs.loading.openModal();
    self.refs.popupMessage.showMessage("now converting...");
        var data = {
            companyid: auth.getCompanyid(),
            userid: auth.getUserid(),
            companyid: auth.getCompanyid(),
            converterId: this.refs.converterEngine.value,
            content: this.refs.htmlcontent.value,
            code: 10
        };

        $.ajax({
                    url: "../commonModules/php/modules/GML.php/gml/data/htmlconverterengine",
                    type: "post",
                    data: JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    headers: {
                      Authorization: "Bearer " + auth.getToken()
                    },
                    success: function(response) {
                      if (response.body.code == 401) {
                        auth.logout();
                      }
                    },
                    error: function(request, status, error) {
                      alert("failed to do testing. Contact Administrator");
                      console.log(status);
                      console.log(error);
                    },
        }).done((data, textStatus, jqXHR) => {
                      self.refs.loading.closeModal();
                      self.refs.popupMessage.closeMessage("finished converting !");

                      console.log("done testing");
                      console.log(data);
        })
  }

  render() {
    return (
      <div className={styles.htmlConverterBox}>
        <select ref="converterEngine" className={ styles.selectMenuDataCrawlerEngines }>
                  <option value="" disabled selected>Select Converter Engine</option>
                      { this.state.converterEngines.map((d, idx) => {
                                        return <option value={ d } key={ "seg" + d }>
                                                 { d }
                                               </option>
                  }) }
        </select>
        <div onClick={ this.execute } className={ styles.htmlConverterExecuteButtonBox }>
            Execute
        </div>
        <textarea ref="htmlcontent" placeholder="Paste Html Tag Here" name="kanso" rows="50" cols="100"></textarea>

        <Loading ref="loading" />
        <PopupMessage ref="popupMessage" />
      </div>
    )
  }
}
