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
            <div className={styles.searchResultBox} onClick={this.clickCallBack}>
                <div className={styles.searchResultBoxInner} >
                    <div className={styles.searchResultBoxInfo}>
                    {this.props.recordInfo.modelname}
                    <br/>
                    {this.props.recordInfo.modeltag}
                    <br/>
                    {this.props.recordInfo.timestamp}
                    <br/>
                    {this.props.recordInfo.algorithm}
                    </div>
                </div>
            </div>
           )
    }
}