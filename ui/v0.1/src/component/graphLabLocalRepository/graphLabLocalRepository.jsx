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
           var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                code:10
            };

            var self = this;
            $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/model/list",
                type : "post",
                data : JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(response) {
                    var modelRecords = [];

                    console.log("local repository");
                    console.log(response);
                    for(let model of response.body.models) {
                        var modelInfo = JSON.parse(model);

                        console.log(modelInfo);
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
            console.log("open : "+recordInfo.modelid);
            console.log(recordInfo);
            var data = {
                companyid: auth.getCompanyid(),
                userid:auth.getUserid(),
                token: auth.getToken(),
                code:10,
                modelid: recordInfo.modelid,
                datetime: recordInfo.datetime
            };
            $.ajax({
                    url  : "../commonModules/php/modules/GML.php/gml/model/get",
                    type : "post",
                    data : JSON.stringify(data),
                    contentType: 'application/json',
                    dataType: "json",
                    success: function(response) {

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
                        console.log(request);
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