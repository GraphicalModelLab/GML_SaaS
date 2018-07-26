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

export default class ModelHistory extends React.Component<Props, {}> {

   constructor(props) {
        super(props);
        this.state = {
                modalIsOpen: false,
                data: {label: 'test accuracy history', values: [
                                {x: new Date(2015, 2, 5), y: 1},
                                {x: new Date(2015, 2, 6), y: 2},
                                {x: new Date(2015, 2, 7), y: 0},
                                {x: new Date(2015, 2, 8), y: 3},
                                {x: new Date(2015, 2, 9), y: 2}
                            ]},
                            xScale: d3.time.scale().domain([new Date(2015, 2, 5), new Date(2015, 2, 26)]).range([0, 400 - 70]),
                            xScaleBrush: d3.time.scale().domain([new Date(2015, 2, 5), new Date(2015, 2, 26)]).range([0, 400 - 70])

        };

        this.openModal = this.openModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
   }

    openModal(model_userid,modelid) {
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
                    console.log("success for save");
                    console.log(response);
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
                        <LineChart
                            data={this.state.data}
                            width={600}
                            height={400}
                            margin={{top: 10, bottom: 50, left: 50, right: 20}}
                            xScale={this.state.xScale}
                            xAxis={{tickValues: this.state.xScale.ticks(d3.time.day, 5), tickFormat: d3.time.format("%m/%d")}}
                        />

                    </Modal>
              </div>
    }
}