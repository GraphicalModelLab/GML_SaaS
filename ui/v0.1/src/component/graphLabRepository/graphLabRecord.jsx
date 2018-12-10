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
import ModelHistoryDialog from './../modelHistory/modelHistoryDialog'

export default class GraphLabRecord extends React.Component<Props, {}> {

    constructor(props) {
        super(props);

        this.clickCallBack = this.clickCallBack.bind(this);
        this.showHistoryCallBack = this.showHistoryCallBack.bind(this);
    }

    clickCallBack(){
        this.props.clickCallBack(this.props.recordInfo);
    }

    showHistoryCallBack(){
        this.refs.modelHistoryDialog.openModal(this.props.recordInfo.userid,this.props.recordInfo.modelid);
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

                    <span className={styles.searchResultBoxAlgorithm}>

                    {this.props.recordInfo.algorithm}
                    </span>
                    <img onClick={this.showHistoryCallBack} src="../icon/history.png" className={styles.searchResultBoxHistoryIcon}/>

                   <img onClick={this.clickCallBack} src="../icon/Right-Arrow-02.png" className={styles.searchResultBoxRightArrowIcon}/>

                   <ModelHistoryDialog ref="modelHistoryDialog" />
            </div>
           )
    }
}