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
import HorizontalScroll from 'react-scroll-horizontal'

export default class DataCrawlerViaSearchEngine extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      crawlerSgs: [],
      sgCrawlerHeader : [],
      sgCrawlerBody : [],
      sgCrawlerLink : [],

      newColumnFirst: [],
      newColumnSecond: [],
      newColumnThird: [],
      newColumnFourth: [],

      crawlerScrapings: [],

      crawlers: []
    };

    this.onDropAttributeImport = this.onDropAttributeImport.bind(this);
    this.onDropApplyingCSV = this.onDropApplyingCSV.bind(this);
    this.sgHeaderClick = this.sgHeaderClick.bind(this);
    this.addColumn = this.addColumn.bind(this);
    this.deleteNewColumn = this.deleteNewColumn.bind(this);
    this.syncNewColumnValues = this.syncNewColumnValues.bind(this);
    this.reflectStateNewColumnValues = this.reflectStateNewColumnValues.bind(this);
    this.findValueByScraping = this.findValueByScraping.bind(this);
  }

  onDropAttributeImport(acceptedFiles, rejectedFiles) {
    var reader = new FileReader();

    var self = this;
    reader.onload = function(e) {
      var text = reader.result; // the entire file

      var firstLine = text.split(/\r\n|\r|\n/); // first line

      var index = 0;
      var firstLineHeader = [];
      firstLine[0].split(',').forEach(function(entry) {
              firstLineHeader.push(entry);

              index += 1;
      });

      var secondLine = [];
      var crawlerLink = [];
      firstLine[1].split(',').forEach(function(entry) {
              secondLine.push(entry);
              crawlerLink.push("");

              index += 1;
      });


      self.setState({
        sgCrawlerHeader: firstLineHeader,
        sgCrawlerBody: secondLine,
        sgCrawlerLink: crawlerLink
      });
    }

    reader.readAsText(acceptedFiles[0], 'UTF-8');
  }

  sgHeaderClick(cb) {
    console.log(cb.target);
    console.log("Clicked, new value = " + cb.target.checked+","+cb.target.name+","+cb.target.value);

    if(cb.target.checked == true){
        var index = cb.target.value;
        var data = {
          code: 10,
          userid: auth.getUserid(),
          companyid: auth.getCompanyid(),
          searchEngineId: this.refs.searchEngine.value,
          query: this.state.sgCrawlerBody[index]
        };

        console.log("post to crawler search engine");
        console.log(JSON.stringify(data));
        var self = this;
        $.ajax({
          url: "../commonModules/php/modules/GML.php/gml/data/crawlersearchengine",
          type: "post",
          data: JSON.stringify(data),
          contentType: 'application/json',
          dataType: "json",
          headers: {
            Authorization: "Bearer " + auth.getToken()
          },
          success: function(response) {
            console.log("success for save");
            console.log(response);
            if (response.body.code == 401) {
              auth.logout();
            }
            console.log(self.state.sgCrawlerLink);
            console.log("index : "+index);
            var sgCrawlerLink = self.state.sgCrawlerLink;
            sgCrawlerLink[index] = response.body.links[0];
            self.setState({
                sgCrawlerLink: sgCrawlerLink
            });

            console.log(self.state.sgCrawlerLink);
          },
          error: function(request, status, error) {
            alert("error");
            console.log(request);
            console.log(status);
            console.log(error);
          }
        });
    }
  }

  deleteNewColumn(deleteIndex){
    this.syncNewColumnValues();

    console.log("delete:"+deleteIndex);
    var newColumnFirst = [];
    var newColumnSecond = [];
    var newColumnThird = [];
    var newColumnFourth = [];

    var decrementIndex = false
    for (let index in this.state.newColumnFirst) {
      if (index != deleteIndex) {
        if(decrementIndex == true){
            this.state.newColumnFirst[index].key = this.state.newColumnFirst[index].key - 1;
            this.state.newColumnSecond[index].key = this.state.newColumnSecond[index].key - 1;
            this.state.newColumnThird[index].key = this.state.newColumnThird[index].key - 1;
            this.state.newColumnFourth[index].key = this.state.newColumnFourth[index].key - 1;

        }
        console.log("added "+index);
        newColumnFirst.push(this.state.newColumnFirst[index]);
        newColumnSecond.push(this.state.newColumnSecond[index]);
        newColumnThird.push(this.state.newColumnThird[index]);
        newColumnFourth.push(this.state.newColumnFourth[index]);
      }else{
        decrementIndex = true;
      }
    }

    var self = this;
    this.setState({
        newColumnFirst: newColumnFirst,
        newColumnSecond: newColumnSecond,
        newColumnThird: newColumnThird,
        newColumnFourth: newColumnFourth
    },function(){
        self.reflectStateNewColumnValues();
    });
  }

  addColumn(){
    this.state.newColumnFirst.push({
        name: "",
        value: "",
        key: this.state.newColumnFirst.length
    });

    this.state.newColumnSecond.push({
        name: "",
        value: "",
        key: this.state.newColumnSecond.length
    });

    this.state.newColumnThird.push({
        name: "",
        value: "",
        key: this.state.newColumnThird.length
    });

    this.state.newColumnFourth.push({
            name: "",
            value: "",
            key: this.state.newColumnFourth.length
    });

    this.setState({
          newColumnFirst: this.state.newColumnFirst,
          newColumnSecond: this.state.newColumnSecond,
          newColumnThird: this.state.newColumnThird,
          newColumnFourth: this.state.newColumnFourth
    });
  }

  componentDidMount() {
    console.log("component Did mount ");
    console.log(this.props.location);

    var self = this;

    $.ajax({
          url: "../commonModules/php/modules/GML.php/gml/data/crawlersearchengine/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
          type: "get",
          headers: {
            Authorization: "Bearer " + auth.getToken()
          },
          success: function(response) {
            console.log("sg list");
            console.log(response);
            if (response.body.code == 401) {
              auth.logout();
            }

            self.setState({
              crawlerSgs: response.body.searchEngineIds
            });
          },
          error: function(request, status, error) {
            alert("error");
            console.log(request.responseText);
            console.log(status);
            console.log(error);
          }
    });

    $.ajax({
              url: "../commonModules/php/modules/GML.php/gml/data/crawlerscrapingengine/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
              type: "get",
              headers: {
                Authorization: "Bearer " + auth.getToken()
              },
              success: function(response) {
                console.log("sg list");
                console.log(response);
                if (response.body.code == 401) {
                  auth.logout();
                }

                self.setState({
                  crawlerScrapings: response.body.scrapingEngineIds
                });
              },
              error: function(request, status, error) {
                alert("error");
                console.log(request.responseText);
                console.log(status);
                console.log(error);
              }
    });

    $.ajax({
        url: "../commonModules/php/modules/GML.php/gml/data/crawlerengine/list?companyid=" + auth.getCompanyid() + "&userid=" + auth.getUserid(),
        type: "get",
        headers: {
            Authorization: "Bearer " + auth.getToken()
        },
        success: function(response) {
                    console.log("crawler list");
                    console.log(response);
                    if (response.body.code == 401) {
                      auth.logout();
                    }

                    self.setState({
                      crawlers: response.body.crawlerEngineIds
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

  reflectStateNewColumnValues(){

      this.state.newColumnFirst.map((d, idx) => {
        this.refs["newColumnFirst"+ idx].value = this.state.newColumnFirst[idx].value;
      });

      this.state.newColumnSecond.map((d, idx) => {
        this.refs["newColumnSecond"+ idx].value = this.state.newColumnSecond[idx].value;
      });

      this.state.newColumnThird.map((d, idx) => {
        this.refs["newColumnThird"+ idx].value = this.state.newColumnThird[idx].value;
      });
  }

  syncNewColumnValues() {
      this.state.newColumnFirst.map((d, idx) => {
        this.state.newColumnFirst[idx].value = this.refs["newColumnFirst"+ idx].value;
      });

      this.state.newColumnSecond.map((d, idx) => {
        this.state.newColumnSecond[idx].value = this.refs["newColumnSecond"+ idx].value;
      });

      this.state.newColumnThird.map((d, idx) => {
        this.state.newColumnThird[idx].value = this.refs["newColumnThird"+ idx].value;
      });
  }

  findValueByScraping(index){

    var sourceColumn = this.refs["newColumnFirst"+ index].value;
    var query = this.refs["newColumnThird"+ index].value;

    var self = this;
    this.state.sgCrawlerHeader.map((d, idx) => {
        console.log(d+">"+idx);
        if(d == sourceColumn){

            var data = {
                code: 10,
                userid: auth.getUserid(),
                companyid: auth.getCompanyid(),
                scrapingEngineId: this.refs.scrapingEngine.value,
                url: self.state.sgCrawlerLink[idx],
                query: query
            };

            $.ajax({
                      url: "../commonModules/php/modules/GML.php/gml/data/crawlerscrapingengine",
                      type: "post",
                      data: JSON.stringify(data),
                      contentType: 'application/json',
                      dataType: "json",
                      headers: {
                        Authorization: "Bearer " + auth.getToken()
                      },
                      success: function(response) {
                        console.log("success for save");
                        console.log(response);
                        if (response.body.code == 401) {
                          auth.logout();
                        }

                        self.refs["newColumnFourth"+ index].value = response.body.data;
                      },
                      error: function(request, status, error) {
                        alert("error");
                        console.log(request);
                        console.log(status);
                        console.log(error);
                      }
            });
        }
    });
  }

  onDropApplyingCSV(acceptedFiles, rejectedFiles) {
    var self = this;

    self.refs.loading.openModal();
    self.refs.popupMessage.showMessage("now crawling...");

    var formData = new FormData();
    formData.append('file_1', acceptedFiles[0]);

    $.ajax({
        url: "../commonModules/php/modules/Uploader.php",
                  type: "POST",
                  data: formData,
                  cache: false,
                  contentType: false,
                  processData: false,
                  dataType: "text",
                  success: function() {},
                  error: function(request, status, error) {
                    alert("failed to upload files for testing");
                    console.log(status);
                    console.log(error);
        },
    }).done((data, textStatus, jqXHR) => {
        var newColumnInformation = [];
        self.state.newColumnFirst.map((d, idx) => {
            newColumnInformation.push({
                        sourceColumn: self.refs["newColumnFirst"+ idx].value,
                        newColumnTitle: self.refs["newColumnSecond"+ idx].value,
                        newColumnQuery: self.refs["newColumnThird"+ idx].value,
            });
        });

        console.log("new Column Query");
        console.log(newColumnInformation);

        var data = {
            companyid: auth.getCompanyid(),
            userid: auth.getUserid(),
            companyid: auth.getCompanyid(),
            scrapingEngineId: this.refs.scrapingEngine.value,
            searchEngineId: this.refs.searchEngine.value,
            crawlerEngineId: this.refs.crawlerEngine.value,
            datasource: data,
            newColumns: newColumnInformation,
            code: 10
        };

        $.ajax({
                    url: "../commonModules/php/modules/GML.php/gml/data/crawlerengine",
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
            self.refs.popupMessage.closeMessage("finished crawling !");
        })
    })
  }

  render() {

    return (

      <div>
        <div className={ styles.dataCrawlerTrialBox }>
          <Dropzone className={ styles.dataCrawlerImportCSV } onDrop={ this.onDropAttributeImport } accept="text/csv">
            <div>
                <img src="./../icon/graphlab_menu_icons/importAttrs.png" className={ styles.dataCrawlerIcon } data-tip="Import Attribute Information from CSV file" />
            </div>
           </Dropzone>
           <select ref="searchEngine" className={ styles.selectMenuDataCrawlerEngines }>
            <option value="" disabled selected>Select Search Engine</option>
                { this.state.crawlerSgs.map((d, idx) => {
                                  return <option value={ d } key={ "seg" + d }>
                                           { d }
                                         </option>
            }) }
           </select>
           <select ref="scrapingEngine" className={ styles.selectMenuDataCrawlerEngines }>
                       <option value="" disabled selected>Select Scraping Engine</option>
                           { this.state.crawlerScrapings.map((d, idx) => {
                                             return <option value={ d } key={ "sce" + d }>
                                                      { d }
                                                    </option>
                       }) }
           </select>

           <img onClick={ this.addColumn } src="./../icon/mono_icons/plus32.png" className={ styles.icon }/>
           <div className={ styles.sgCrawlerTableScroller }>
           <HorizontalScroll>
           <div className={ styles.sgCrawlerTableScrollerDiv }>
           <table className={ styles.sgCrawlerTable }>
           <tr>
                       { this.state.sgCrawlerBody.map((d, idx) => {
                           return <th key={ "sgCrawlerCheckbox" + d } >
                                    <input className={ styles.dataCrawlerViaSearchEngineInput } type="checkbox" name={d} value={idx} onChange={(e) => {
                                                               this.sgHeaderClick(e);
                                                             }} />
                                  </th>
                       }) }
                       { this.state.newColumnFirst.map((d, idx) => {
                           return <th key={ "sgCrawlerCheckboxNewColumn" + d.key } >
                               <img key={d.key} onClick={ (e)=> {this.deleteNewColumn(d.key); }} src="./../icon/mono_icons/minus32.png" className={ styles.icon }/>
                               <input className={ styles.dataCrawlerViaSearchEngineInput } ref={"newColumnFirst"+idx} type="text" defaultValue={d.value} name="name" size="10" />
                            </th>
                       }) }
           </tr>
            <tr>
                { this.state.sgCrawlerHeader.map((d, idx) => {
                    return <th key={ "sgCrawlerHeader" + idx } >
                                   <input readOnly className={ styles.dataCrawlerViaSearchEngineInputReadOnly } ref={"ColumnSecond"+idx} type="text" value={d} size="10" />
                           </th>
                }) }
                { this.state.newColumnSecond.map((d, idx) => {
                           return <th key={ "sgCrawlerHeaderNewColumn" + d.key } >
                           <input className={ styles.dataCrawlerViaSearchEngineInput } ref={"newColumnSecond"+idx} type="text" defaultValue={d.value} name="name" size="10" />
                                  </th>
                }) }
            </tr>
            <tr>
            { this.state.sgCrawlerBody.map((d, idx) => {
                return <td key={ "sgCrawlerBody" + idx } >
                                <input readOnly className={ styles.dataCrawlerViaSearchEngineInputReadOnly } ref={"ColumnSecond"+idx} type="text" value={d} size="10" />
                       </td>
            }) }
                       { this.state.newColumnThird.map((d, idx) => {
                           return <td key={ "sgCrawlerBodyNewColumn" + d.key } >
                           <img src="./../icon/menu_icons/search.png" onClick={(e)=> { this.findValueByScraping(d.key) }} className={ styles.searchIconDataCrawler } />
                           <input className={ styles.dataCrawlerViaSearchEngineInput } ref={"newColumnThird"+idx} type="text" defaultValue={d.value} name="name" size="10" />
                                  </td>
                       }) }
            </tr>
            <tr>
            { this.state.sgCrawlerLink.map((d, idx) => {
                return <td key={ "sgCrawlerLink" + idx } >
                                <a href={d}>{d}</a>
                       </td>
            }) }

                       { this.state.newColumnFourth.map((d, idx) => {
                           return <td key={ "sgCrawlerBodyNewColumn" + d.key } >
                           <input className={ styles.dataCrawlerViaSearchEngineInput } ref={"newColumnFourth"+idx} type="text" name="name" size="10" />
                                  </td>
                       }) }
            </tr>
           </table>
           </div>
           </HorizontalScroll>
           </div>
        </div>
        <div className={ styles.dataCrawlerApplyRuleBox }>
        Apply these rules to
        <Dropzone className={ styles.dataCrawlerTargetCSV } onDrop={ this.onDropApplyingCSV } accept="text/csv">
            <div>
                <img src="./../icon/graphlab_menu_icons/importAttrs.png" className={ styles.dataCrawlerIcon } data-tip="Apply this crawling rule to the selected file" />
            </div>
        </Dropzone>
        via
        <select ref="crawlerEngine" >
            <option value="" disabled selected>Select Crawler Engine</option>
                { this.state.crawlers.map((d, idx) => {
                                  return <option value={ d } key={ "crawler" + d }>
                                           { d }
                                         </option>
            }) }
           </select>
        </div>
        <Loading ref="loading" />
        <PopupMessage ref="popupMessage" />
      </div>
    )
  }
}
