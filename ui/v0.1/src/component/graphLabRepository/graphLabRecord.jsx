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

        this.recordEnterCallBack = this.recordEnterCallBack.bind(this);

    }


    recordEnterCallBack(){
        this.props.recordEnterCallBack(this.props.recordInfo);
    }

    render() {
        return (
            <g onMouseEnter={this.recordEnterCallBack}>
            <image href="../icon/apple.gif" width="8%" height="8%" x={this.props.coordinate_x + this.props.x} y={this.props.coordinate_y + this.props.y} />
            </g>
           )
    }
}