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
import * as styles from './../../css/dataPlayground.css';
import auth from "./../auth/auth";
import Dropzone from 'react-dropzone';
import $ from 'jquery';
import Loading from './../loader/loading';
import DataCrawlerViaSearchEngine from './dataCrawlerViaSearchEngine/dataCrawlerViaSearchEngine';
import DataExtractor from './dataExtractor/dataExtractor';
import HtmlConverter from './htmlConverter/htmlConverter';

export default class DataPalyground extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      extractors: [],
      extractorParamMap: {},

      crawlerSgs: [],
      sgCrawlerHeader : [],
      sgCrawlerBody : [],
      sgCrawlerLink : [],
      newColumn: [],
      currentPanel: "exploration"
    };

    this.showDataExploration = this.showDataExploration.bind(this);
    this.showDataCrawler = this.showDataCrawler.bind(this);
    this.showDataExtractor = this.showDataExtractor.bind(this);
    this.showHtmlConverter = this.showHtmlConverter.bind(this);
  }

  showDataExploration(){
    this.setState({
        currentPanel: "exploration"
    });
  }

  showDataCrawler(){
    this.setState({
        currentPanel: "crawler"
    });
  }

  showDataExtractor(){
    this.setState({
        currentPanel: "extractor"
    });
  }

  showHtmlConverter(){
    this.setState({
          currentPanel: "htmlconverter"
    });
  }

  componentDidMount() {
  }

  render() {
    return (
      <div>
        <div className={styles.playGroundMenu}>
            <div className={this.state.currentPanel == "exploration" ? styles.playGroundMenuItemChosen : styles.playGroundMenuItem } onClick={ this.showDataExploration }>
                Data Exploration
            </div>
            <div className={this.state.currentPanel == "extractor" ? styles.playGroundMenuItemChosen : styles.playGroundMenuItem } onClick={ this.showDataExtractor }>
                Data Extractor
            </div>
            <div className={this.state.currentPanel == "crawler" ? styles.playGroundMenuItemChosen : styles.playGroundMenuItem } onClick={ this.showDataCrawler }>
                Data Crawler via Search Engine
            </div>
            <div className={this.state.currentPanel == "htmlconverter" ? styles.playGroundMenuItemChosen : styles.playGroundMenuItem } onClick={ this.showHtmlConverter }>
                Html Converter
            </div>
        </div>
        <div className={styles.playGroundBody}>
            {this.state.currentPanel == "exploration" ?
            (<div></div>)
            :(
                this.state.currentPanel == "crawler" ? (
                <DataCrawlerViaSearchEngine/>
                ):(
                   this.state.currentPanel == "extractor" ? (
                    <DataCrawlerViaSearchEngine/>
                   ):(
                    <HtmlConverter/>
                   )
                )
            )
            }
        </div>
        <Loading ref="loading" />
      </div>
    )
  }
}
