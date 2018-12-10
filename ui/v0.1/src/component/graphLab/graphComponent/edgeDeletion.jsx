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
import NodePropertyView from './../graphProperty/nodePropertyView'
import color from "./../graphColor/color";

export default class EdgeDeletion extends React.Component<Props, {}> {

   constructor(props) {
        super(props);

        this.state = {
                     x: this.props.x,
                     y: this.props.y
                   };

        this.deleteEdge = this.deleteEdge.bind(this);
        this.update = this.update.bind(this);
        this.deleteEdge = this.deleteEdge.bind(this);
   }

   update(x,y){
        this.setState({
                  x: x,
                  y: y
        });
   }

   deleteEdge(e){
      e.stopPropagation();

      this.props.deleteEdgeCallBack(this.props.label1, this.props.label2);
   }

   render() {
        return (
               <g onClick={this.deleteEdge}>
                    <circle
                        r="8"
                        cx={this.state.x}
                        cy={this.state.y}
                        fill={ color.get() }
                    ></circle>

                    <line stroke="white" x1={this.state.x-3} y1={this.state.y-3} x2={this.state.x+3} y2={this.state.y+3} />

                    <line stroke="white" x1={this.state.x+3} y1={this.state.y-3} x2={this.state.x-3} y2={this.state.y+3} />

               </g>
           )
   }
}