import * as React from 'react';
import * as ReactDOM from 'react-dom';
import * as styles from './../../css/structure.css';
import auth from "./../auth/auth";
import $ from 'jquery';
import Loading from './../loader/loading';

export default class NotFound extends React.Component<Props, {}> {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className={styles.notFound}>
                                Not Found
            </div>
           )
    }
}