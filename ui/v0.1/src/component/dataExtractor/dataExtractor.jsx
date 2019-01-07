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
import ExtractorPanel from './panel/extractorPanel';

export default class DataExtractor extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      extractors: [],
      extractorParamMap: {}
    };

  }

  componentDidMount() {
    console.log("component Did mount ");
    console.log(this.props.location);

    var self = this;
    $.ajax({
      url: "../commonModules/php/modules/GML.php/gml/data/extractor/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
      type: "get",
      headers: {
        Authorization: "Bearer " + auth.getToken()
      },
      success: function(response) {
        console.log("extractor list");
        console.log(response);
        if (response.body.code == 401) {
          auth.logout();
        }

        self.setState({
          extractors: response.body.extractorIds,
          extractorParamMap: response.body.extractorParamMap
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

  render() {
    return (
      <div>
        <div className={ styles.dataExplorationBox }>
          <div className={ styles.dataExplorationTitleHeader }>Data Exploration/Extraction</div>
        </div>
        <div className={ styles.dataExtractorBox }>
          { this.state.extractors.map((d, idx) => {
              return <ExtractorPanel panelTitle={ d } key={ "evaluation" + d } params={ this.state.extractorParamMap[d] }>
                       { d }
                     </ExtractorPanel>
            }) }
        </div>
        <Loading ref="loading" />
      </div>
    )
  }
}
