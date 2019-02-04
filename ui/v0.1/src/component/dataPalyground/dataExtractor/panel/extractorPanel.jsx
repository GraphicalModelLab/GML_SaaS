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
import * as styles from './../../../../css/dataPlayground.css';
import auth from "./../../../auth/auth";
import $ from 'jquery';
import DataExtractorPropertyView from './../dataExtractorProperty/dataExtractorPropertyView';

export default class ExtractorPanel extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      modelparameter: []
    };

    this.showDataExtractorPropertyView = this.showDataExtractorPropertyView.bind(this);
  }

  showDataExtractorPropertyView() {
    console.log(this.props.params);
    var params = [];

    this.props.params.forEach(function(entry) {
      params.push({
        label: entry
      });
    });
    this.setState({
      modelparameter: params,
    });

    this.refs.dataExtractorPropertyView.openModal();
  }

  render() {

    return (
      <div className={ styles.extractorPanel } onClick={ this.showDataExtractorPropertyView }>
        { this.props.panelTitle }
        <DataExtractorPropertyView label="Settings for Extractor" ref="dataExtractorPropertyView" modelparameter={ this.state.modelparameter } extractorId={ this.props.panelTitle } />
      </div>
    )
  }
}
