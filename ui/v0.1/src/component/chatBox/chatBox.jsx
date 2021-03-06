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
import * as styles from './../../css/chatbox.css';
import ChatElement from './chatElement';
import ChatWritingElement from './chatWritingElement';

export default class ChatBox extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      showChatChannel: false,
      chatcontent: []
    };
    this.showChatChannel = this.showChatChannel.bind(this);
    this.keyPress = this.keyPress.bind(this);
    this.removeWritingStatus = this.removeWritingStatus.bind(this);
  }

  showChatChannel(e) {
    this.setState({
      showChatChannel: !this.state.showChatChannel
    });
  }

  removeWritingStatus() {}

  keyPress(e) {
    if (e.charCode == 13) {
      this.state.chatcontent.push({
        index: this.state.chatcontent.length,
        type: "user",
        content: this.refs.chatboxInput.value
      });

      this.state.chatcontent.push({
        index: this.state.chatcontent.length,
        type: "ai",
        content: "まだ、実装中です。しばし、お待ちください"
      });

      this.setState({
        showChatChannel: this.state.showChatChannel,
        chatcontent: this.state.chatcontent
      });

      this.refs.chatboxInput.value = "";
    }
  }

  render() {
    return <div className={ this.state.showChatChannel ? styles.chatboxOpen : styles.chatboxClosed }>
             <div className={ styles.chatboxTitle } onClick={ this.showChatChannel.bind(this) }>質問</div>
             { this.state.showChatChannel == true ? (
               <div>
                 <div className={ styles.chatboxDisp }>
                   { this.state.chatcontent.map(function(chatcontent) {
                       return <ChatElement key={ chatcontent.index } content={ chatcontent.content } type={ chatcontent.type } />
                     }, this) }
                 </div>
                 <input type="text" className={ styles.chatboxInput } ref="chatboxInput" onKeyPress={ this.keyPress } />
               </div>
               ) : (<div></div>) }
           </div>
  }
}
