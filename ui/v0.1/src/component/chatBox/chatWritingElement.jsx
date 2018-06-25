import * as React from 'react';
import * as styles from './../../css/chatbox.css';

export default class ChatWritingElement extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return <div className={styles.loader}>
                    <span></span>
                    <span></span>
                    <span></span>
               </div>
    }
}