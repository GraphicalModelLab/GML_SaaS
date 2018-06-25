import * as React from 'react';
import * as styles from './../../css/chatbox.css';
import ChatWritingElement from './chatWritingElement';

export default class ChatElement extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
    }
    render() {
        return <div className={styles.chatElement}>
                  <span className={this.props.type == "user" ? styles.chatCommentRightElement: styles.chatCommentLeftElement}>
                     {this.props.content == "writing" ? (
                       <span> <ChatWritingElement /> </span>
                     ):(<span>{this.props.content}</span>)}
                  </span>
               </div>
    }
}