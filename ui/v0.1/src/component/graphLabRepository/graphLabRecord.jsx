import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Dropzone from 'react-dropzone';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class GraphLabRecord extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.clickCallBack = this.clickCallBack.bind(this);

        console.log(this.props.recordInfo);
    }

    clickCallBack(){
        this.props.clickCallBack(this.props.recordInfo);
    }

    render() {
        return (
            <div className={styles.searchResultBox}>
                <img src="../icon/flask.png" className={styles.searchResultBoxFlaskIcon}/>
                    <span className={styles.searchResultBoxModelName}>
                    {this.props.recordInfo.modelname}
                    </span>
                    <span className={styles.searchResultBoxModelTag}>
                      {this.props.recordInfo.modeltag}
                    </span>
                    <span className={styles.searchResultBoxTimeStamp}>
                    {this.props.recordInfo.timestamp}
                    </span>

                    <span className={styles.searchResultBoxAlgorithm}>

                    {this.props.recordInfo.algorithm}
                    </span>

                   <img onClick={this.clickCallBack} src="../icon/Right-Arrow-02.png" className={styles.searchResultBoxRightArrowIcon}/>
            </div>
           )
    }
}