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
import Modal from 'react-modal';
import auth from "./../auth/auth";
import $ from 'jquery';
import * as styles from './../../css/structure.css';
import { ScatterPlot } from 'react-d3-components'
import TestHistoryRecordLine from './testHistoryRecordLine';

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    height                : '500px',
    width                 : '800px'
  }
};

export default class ModelHistoryDialogWithSearch extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                plotTestHistory: false,
                records: []
        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.tooltipScatter = this.tooltipScatter.bind(this);
        this.openGraph = this.openGraph.bind(this);
   }

    openModal(model_userid,modelid) {
        var self = this;
        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true}, function(){
            $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/model/test/history/list?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid()+"&model_userid="+model_userid+"&modelid="+modelid,
                type : "get",
                headers : {
                    Authorization: "Bearer "+auth.getToken()
                },
                success: function(response) {

                    var values = [];
                    var records = [];
                    var oldestDate = new Date();

                    // JSON.parse(response.body.model)
                    console.log("success for save");
                    console.log(response);

                    if(response.body.history.length > 0){
                        var n = 4;
                        for(let index in response.body.history){
                            var json = JSON.parse(response.body.history[index]);
                            json.info.accuracy = Math.floor( json.info.accuracy * Math.pow( 10, n ) ) / Math.pow( 10, n );

                            var date = new Date(json.time);

                            var format_str = 'YYYY/MM/DD hh:mm:ss';
                            format_str = format_str.replace(/YYYY/g, date.getFullYear());
                            format_str = format_str.replace(/MM/g, date.getMonth());
                            format_str = format_str.replace(/DD/g, date.getDate());
                            format_str = format_str.replace(/hh/g, date.getHours());
                            format_str = format_str.replace(/mm/g, date.getMinutes());
                            format_str = format_str.replace(/ss/g, date.getSeconds());

                            json.model.formattedDate = format_str;

                            records.push(json);

                            values.push({
                                x: date, y: json.info.accuracy
                            });

                            if(date < oldestDate){
                                oldestDate = date;
                            }
                        }

                        var earliestDate = oldestDate;

                        for(let index in values){
                            if(values[index].x > earliestDate){
                                earliestDate = values[index].x;
                            }
                        }

                        var axisOldest = new Date(oldestDate.getTime()); axisOldest.setDate(oldestDate.getDate() - 2);
                        var axisEarliest = new Date(earliestDate.getTime()); axisEarliest.setDate(earliestDate.getDate() + 2);

                        self.setState({
                            plotTestHistory: true,
                            data: {label: 'test accuracy history', values: values},
                            xScale: d3.time.scale().domain([axisOldest, axisEarliest]).range([0, 1000 - 0]),
                            xScaleBrush: d3.time.scale().domain([axisOldest, axisEarliest]).range([0, 1000 - 0]),
                            records: records
                        });

                        console.log(self.state.data);
                    }
                },
                error: function(request, status, error) {
                    alert("error");
                    console.log(request.responseText);
                    console.log(status);
                    console.log(error);
                }
            });

        });
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }

    tooltipScatter(x, y) {
        var n = 2;
        return (Math.floor( y * Math.pow( 10, n ) ) / Math.pow( 10, n ))+".., "+x.getFullYear() + "/" +  (x.getMonth() + 1) + "/"+ x.getDate()+" "+x.getHours()+":"+x.getMinutes();
    }

    openGraph(recordInfo){
        console.log(recordInfo);

        var self = this;

         if(recordInfo.model.modelid){
             console.log("open : "+recordInfo.model.modelid);
             console.log(recordInfo);
             var data = {
                 companyid: auth.getCompanyid(),
                 userid:auth.getUserid(),
                 code:10,
                 modelid: recordInfo.model.modelid,
                 datetime: recordInfo.model.datetime
             };
             $.ajax({
                     url  : "../commonModules/php/modules/GML.php/gml/model/test/history?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid()+"&modelid="+recordInfo.model.modelid+"&datetime="+recordInfo.model.datetime,
                     type : "get",
                    headers : {
                        Authorization: "Bearer "+auth.getToken()
                    },
                     success: function(response) {
                         console.log("got graph");
                         console.log(response);
                         self.props.clear();
                         self.props.setup(JSON.parse(response.body.model));
                         self.closeModal();
                     },
                     error: function (request, status, error) {
                         alert("error");
                         console.log(request);
                         console.log(status);
                         console.log(error);
                     }
             });
         }
    }

    render() {
        return <div>
                    <Modal
                        contentLabel="Model History"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.nodePropertyViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}></div><div onClick={this.closeModal} className={styles.closeButton}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></h2>
                        </div>

                        {this.state.plotTestHistory ? (<ScatterPlot
                            data={this.state.data}
                            width={1000}
                            height={400}
                            tooltipHtml={this.tooltipScatter}
                            margin={{top: 10, bottom: 50, left: 50, right: 20}}
                            xScale={this.state.xScale}
                            xAxis={{tickValues: this.state.xScale.ticks(d3.time.day, 1), tickFormat: d3.time.format("%m/%d")}}
                        />

                        ):(
                            <div></div>
                        )}


                        {this.state.records.map((d, idx) => {
                            return <TestHistoryRecordLine clickCallBack={this.openGraph} key={"record:"+idx} recordInfo={d} />
                        })}
                    </Modal>
              </div>
    }
}
