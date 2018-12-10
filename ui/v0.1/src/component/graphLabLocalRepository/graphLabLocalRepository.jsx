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
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import GraphLabRecord from './../graphLabRepository/graphLabRecord';
import { withRouter } from 'react-router';

export default class GraphicalLabLocalRepository extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
        this.state = {
            records: [],
            notFound: false
        };

        this.openGraph = this.openGraph.bind(this);
        this.setup = this.setup.bind(this);
        this.clear = this.clear.bind(this);
        this.showResult = this.showResult.bind(this);
    }

    componentWillUnmount(){
        window.removeEventListener("resize", this.updateDimensions);
    }
    clear(){
       this.setState({
            records: []
       });
    }

    componentDidMount() {
        //this.clear();
        this.showResult();
    }

    showResult(){

            var self = this;
            $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/model/list?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid(),
                type : "get",
                headers : {
                    Authorization: "Bearer "+auth.getToken()
                },
                success: function(response) {
                    var modelRecords = [];

                    console.log("local repository");
                    console.log(response);
                    for(let model of response.body.models) {
                        var modelInfo = JSON.parse(model);

                        var date = new Date(modelInfo.datetime);

                        var format_str = 'YYYY/MM/DD hh:mm:ss';
                        format_str = format_str.replace(/YYYY/g, date.getFullYear());
                        format_str = format_str.replace(/MM/g, date.getMonth());
                        format_str = format_str.replace(/DD/g, date.getDate());
                        format_str = format_str.replace(/hh/g, date.getHours());
                        format_str = format_str.replace(/mm/g, date.getMinutes());
                        format_str = format_str.replace(/ss/g, date.getSeconds());

                        modelInfo.formattedDate = format_str;
                        console.log("model date:"+modelInfo.formattedDate+":"+date+":"+modelInfo.timestamp);
                        modelRecords.push(modelInfo);
                    }

                    console.log("recodes length ? "+modelRecords.length + ",,"+modelRecords.size);
                    if(modelRecords.length > 0){
                        self.setState({
                                records: modelRecords
                        });
                    }else{
                        self.context.router.push({
                            pathname: '/notFound',
                            state: {}
                        });
                    }
                },
                error: function (request, status, error) {
                    alert("error");
                    console.log(request.responseText);
                    console.log(request);
                    console.log(status);
                    console.log(error);
                }
            });
    }

    setup(models){
        var modelRecords = [];
        for(let modelInfo of models) {
            modelRecords.push(modelInfo);
        }

        this.setState({
            records: modelRecords
        });
    }

    openGraph(recordInfo){
        var self = this;

        if(recordInfo.modelid){
            $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/model?companyid="+auth.getCompanyid()+"&userid="+auth.getUserid()+"&modelid="+recordInfo.modelid,
                    type : "get",
                    headers : {
                        Authorization: "Bearer "+auth.getToken()
                    },
                    success: function(response) {
                        console.log("got response");
                        console.log(response);
                        if(response.body.code == 401){
                            auth.logout();
                        }

                        self.context.router.push({
                            pathname: '/graphLab',
                            state: {
                                graphInfo: JSON.parse(response.body.model)
                            }
                        });
                    },
                    error: function (request, status, error) {
                        alert("error");
                    console.log(request.responseText);
                        console.log(status);
                        console.log(error);
                    }
            });
        }else{
            this.context.router.push({
                            pathname: '/graphLab',
                            state: {
                                graphInfo: recordInfo
                            }
            });
        }
    }

    render() {
        return (
              <div className={styles.searchResults}>
               { this.state.records.map((d, idx) => {
                   return <GraphLabRecord clickCallBack={this.openGraph} key={"record:"+idx} recordInfo={d} x={d.x} y={d.y} coordinate_x={(this.state.svg_width - this.state.tree_width) > 0 ? (this.state.svg_width - this.state.tree_width)/2 + this.state.tree_width/2 : this.state.tree_width/2}  coordinate_y={this.state.tree_height/2} />
               }) }
              </div>
           )
    }
}


GraphicalLabLocalRepository.contextTypes = {
    router: React.PropTypes.object
};