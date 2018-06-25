import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class WebExploration extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div>
                    <div className={styles.accountRoleChangeTitle}> Web探索 </div>
                    <div className={styles.accountRoleChangeItemBox}>
                    <div className={styles.accountRoleChangeItemName}>
                        W
                    </div>
                    </div>
                </div>
                 <Loading ref="loading"/>
            </div>
           )
    }
}