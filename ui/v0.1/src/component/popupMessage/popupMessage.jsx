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
import * as styles from './../../css/popupMessage.css';

export default class PopupMessage extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      show: false,
      message: "",
      closeStyle: false
    };
  }

  componentDidMount() {}

  showMessage(message) {
    this.setState({
      show: true,
      message: message,
      closeStyle: false
    });
  }


  closeMessage(message) {
    this.setState({
      show: true,
      message: message,
      closeStyle: true
    });
    var self = this;
    setTimeout(function(message) {
      self.setState({
        show: false,
        message: "",
        closeStyle: false
      });
    }, 500, message);
  }

  render() {
    return <div>
             { this.state.show ? (
               <div className={ this.state.closeStyle ? styles.popupMessageClose : styles.popupMessage }>
                 { this.state.message }
               </div>
               ) : (<div></div>) }
           </div>
  }
}
