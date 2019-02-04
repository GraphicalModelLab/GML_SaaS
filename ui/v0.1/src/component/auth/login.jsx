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
import { DefaultRoute, Link, Route, RouteHandler } from 'react-router';
import LoginModal from './loginModal';

export default class login extends React.Component {
  constructor(props) {
    super(props);
  }

  componentDidUpdate() {
    this.refs.loadingModal.openModal();
  }

  componentDidMount() {
    this.refs.loadingModal.openModal();
  }


  render() {
    return (
      <div>
        <LoginModal ref="loadingModal" type={ this.props.route.type } />
      </div>
      );
  }
}
