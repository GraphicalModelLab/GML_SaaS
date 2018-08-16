import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';
import GraphLabRecord from './../graphLabRepository/graphLabRecord';
import { withRouter } from 'react-router';

// Tree List  https://www.sozailab.jp/sozai/detail/6152/
//            https://www.google.co.jp/imgres?imgurl=https%3A%2F%2Fpds.exblog.jp%2Fpds%2F1%2F200810%2F13%2F45%2Fd0094245_1032524.gif&imgrefurl=https%3A%2F%2Fpopachi.exblog.jp%2F8757735%2F&docid=c13zelLPL4D8aM&tbnid=zFXSOSvu7c3ZTM%3A&vet=10ahUKEwjn44y9pf_bAhULoJQKHaUUBQwQMwiZASgAMAA..i&w=416&h=414&bih=551&biw=1085&q=%E3%83%AA%E3%83%B3%E3%82%B4%E3%80%80%E7%B5%B5&ved=0ahUKEwjn44y9pf_bAhULoJQKHaUUBQwQMwiZASgAMAA&iact=mrc&uact=8
export default class SearchResult1 extends React.Component<Props, {}> {
constructor(props) {
        super(props);
        this.state = {
            records: []
        };

        this.openGraph = this.openGraph.bind(this);
        this.setup = this.setup.bind(this);
        this.clear = this.clear.bind(this);
        this.showResult = this.showResult.bind(this);
    }

    componentWillUnmount(){
    }
    clear(){
       this.setState({
            records: []
       });
    }

    componentDidMount() {
        this.clear();
        this.showResult();
    }

    showResult(){
        if(this.props.location.state.modelInfo){
            this.setup(this.props.location.state.modelInfo);
        }
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
                        self.context.router.push({
                            pathname: '/graphLab',
                            state: {
                                graphInfo: JSON.parse(response.body.model)
                            }
                        });
                    },
                    error: function (request, status, error) {
                        alert("Failed to get Model Data. Contact Administrator");

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


SearchResult1.contextTypes = {
    router: React.PropTypes.object
};