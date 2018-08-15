import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class TestHistoryRecordLine extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.clickCallBack = this.clickCallBack.bind(this);

    }

    clickCallBack(){
        this.props.clickCallBack(this.props.recordInfo);
    }

    render() {
        return (
            <div className={styles.searchResultBoxHistory}>
                <img src="../icon/flask.png" className={styles.searchResultBoxFlaskIcon}/>
                    <span className={styles.searchResultBoxTimeStamp}>
                    {this.props.recordInfo.model.formattedDate}
                    </span>

                    <span className={styles.searchResultBoxAlgorithm}>
                    {this.props.recordInfo.model.algorithm}
                    </span>

                    <span className={styles.searchResultBoxAccuracy}>
                        {this.props.recordInfo.info.accuracy}
                    </span>

                    <span className={styles.searchResultBoxEvaluationMethod}>
                        {this.props.recordInfo.info.evaluationMethod}
                    </span>

                   <img onClick={this.clickCallBack} src="../icon/Right-Arrow-02.png" className={styles.searchResultBoxRightArrowIcon}/>
            </div>
           )
    }
}