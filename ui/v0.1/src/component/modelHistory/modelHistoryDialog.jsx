import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Modal from 'react-modal';
import auth from "./../auth/auth";
import $ from 'jquery';
import * as styles from './../../css/structure.css';
import { LineChart } from 'react-d3-components'

const customStyles = {
  content : {
    top                   : '50%',
    left                  : '50%',
    right                 : 'auto',
    bottom                : 'auto',
    marginRight           : '-50%',
    transform             : 'translate(-50%, -50%)',
    height                : '400px',
    width                 : '600px'
  }
};

export default class ModelHistoryDialog extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                plotTestHistory: false

        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
   }

    openModal(model_userid,modelid) {
        var self = this;
        // setState is asynchnous. And, DOMs inside Modal are rendered after the completion of setState so that they can be manipulated after setState completion
        this.setState({modalIsOpen: true}, function(){
            alert("opened history modal : "+modelid);
            var data = {
                        companyid: auth.getCompanyid(),
                        userid:auth.getUserid(),
                        token: auth.getToken(),
                        model_userid: model_userid,
                        modelid: modelid,
                        code:10
            };

            $.ajax({
                url  : "../commonModules/php/modules/GML.php/gml/model/history",
                type : "post",
                data : JSON.stringify(data),
                contentType: 'application/json',
                dataType: "json",
                success: function(response) {

                    var values = [];
                    var oldestDate = new Date();

                    // JSON.parse(response.body.model)
                    console.log("success for save");
                    console.log(response);

                    for(let index in response.body.history){
                        var json = JSON.parse(response.body.history[index]);
                        var date = new Date(json.time);
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

                    console.log("values");
                    console.log(values);

                    console.log(earliestDate);
                    console.log(oldestDate);

                    var axisOldest = new Date(oldestDate.getTime()); axisOldest.setDate(oldestDate.getDate() - 2);
                    var axisEarliest = new Date(earliestDate.getTime()); axisEarliest.setDate(earliestDate.getDate() + 2);

                    console.log(axisOldest);
                    console.log(axisEarliest);

                    var newValues = [];

                    while(axisOldest < oldestDate){
                        console.log(axisOldest +" < "+oldestDate+"add oldest : "+axisOldest);
                        newValues.push({
                            x: new Date(axisOldest.getTime()), y: 0
                        });
                        axisOldest.setDate(axisOldest.getDate() + 1);
                    }

                    for(let index in values){
                        newValues.push(values[index]);
                    }

                    var axisEarliest2 = new Date(earliestDate.getTime()); axisEarliest2.setDate(axisEarliest2.getDate() + 1)
                    while(axisEarliest >= axisEarliest2){
                        console.log(axisEarliest +" > "+earliestDate+"add earliest : "+earliestDate);
                        newValues.push({
                            x: new Date(axisEarliest2.getTime()), y: 0
                        });
                        axisEarliest2.setDate(axisEarliest2.getDate() + 1);
                    }

                    console.log(earliestDate);
                    console.log(oldestDate);

                    axisOldest.setDate(oldestDate.getDate() - 2);

                    self.setState({
                        plotTestHistory: true,
                        data: {label: 'test accuracy history', values: newValues},
                        xScale: d3.time.scale().domain([axisOldest, axisEarliest]).range([0, 1000 - 0]),
                        xScaleBrush: d3.time.scale().domain([axisOldest, axisEarliest]).range([0, 1000 - 0])
                    });

                    console.log(self.state.data);
                },
                error: function(request, status, error) {
                    alert("error");
                    console.log(request);
                    console.log(status);
                    console.log(error);
                }
            });

        });
    }

    closeModal() {
        this.setState({modalIsOpen: false});
    }


    render() {
        return <div>
                    <Modal
                        contentLabel="Model Property"
                        isOpen={this.state.modalIsOpen}
                        onAfterOpen={this.afterOpenModal}
                        style={customStyles} ref="modal">

                        <div className={styles.nodePropertyViewTitle}>
                            <h2 ref="subtitle"><div className={styles.modalTitle}></div><div onClick={this.closeModal} className={styles.closeButton}><img src="../icon/mono_icons/stop32.png" className={styles.icon}/></div></h2>
                        </div>

                        {this.state.plotTestHistory ? (<LineChart
                            data={this.state.data}
                            width={1000}
                            height={400}
                            margin={{top: 10, bottom: 50, left: 50, right: 20}}
                            xScale={this.state.xScale}
                            xAxis={{tickValues: this.state.xScale.ticks(d3.time.day, 1), tickFormat: d3.time.format("%m/%d")}}
                        />):(
                            <div></div>
                        )}


                    </Modal>
              </div>
    }
}