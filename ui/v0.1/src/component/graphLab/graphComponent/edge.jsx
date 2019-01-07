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

export default class Circle extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.calculateCircleEdgePoint = this.calculateCircleEdgePoint.bind(this);
    this.trimForMarkerEnd = this.trimForMarkerEnd.bind(this);
    this.update1 = this.update1.bind(this);
    this.update2 = this.update2.bind(this);

    var r_diff = 0; // This is a margin created by putting arrow mark of the edge. If Undirected edge, this value should be 0.

    if(this.props.isDirected) r_diff = 8;

    var new_x_y_2 = this.calculateCircleEdgePoint({
      x: this.props.x1,
      y: this.props.y1
    }, {
      x: this.props.x2,
      y: this.props.y2
    }, 30, r_diff);
    var new_x_y_1 = this.calculateCircleEdgePoint({
      x: this.props.x1,
      y: this.props.y1
    }, {
      x: this.props.x2,
      y: this.props.y2
    }, 30, 0);


    this.state = {
      x1: this.props.x1,
      y1: this.props.y1,
      x2: this.props.x2,
      y2: this.props.y2,
      r_diff: r_diff,

      fixed_x1: this.props.x1 - new_x_y_1.new_x,
      fixed_y1: this.props.y1 - new_x_y_1.new_y,
      fixed_x2: this.props.x2 + new_x_y_2.new_x,
      fixed_y2: this.props.y2 + new_x_y_2.new_y,

      label1: this.props.label1,
      label2: this.props.label2,
      isDirected: this.props.isDirected
    };


  }

  componentDidMount() {
    var markerLine = ReactDOM.findDOMNode(this.refs[this.state.label1 + ":" + this.state.label2])

    markerLine.setAttribute('marker-end', "url(#" + this.trimForMarkerEnd(this.state.label1 + ":" + this.state.label2) + ")")
  }

  trimForMarkerEnd(str) {
    return str.replace(/\s/g, '');
  }

  isLabel1(label) {
    return label == this.state.label1;
  }

  isLabel2(label) {
    return label == this.state.label2;
  }

  /**
   *
   * r = the length of circle
   */
  calculateCircleEdgePoint(currPosition, prevPosition, r, r_diff) {
    r = r + r_diff
    var a = (prevPosition.y - currPosition.y) / (prevPosition.x - currPosition.x);

    var x_diff = Math.sqrt((r * r) / (1 + a * a));
    var y_diff = Math.sqrt((a * a * r * r) / (1 + a * a));

    if (currPosition.x < prevPosition.x && currPosition.y < prevPosition.y) {
      return {
        new_x: -Math.sqrt((r * r) / (1 + a * a)),
        new_y: -Math.sqrt((a * a * r * r) / (1 + a * a))
      }
    } else if (currPosition.x < prevPosition.x && currPosition.y > prevPosition.y) {
      return {
        new_x: -Math.sqrt((r * r) / (1 + a * a)),
        new_y: Math.sqrt((a * a * r * r) / (1 + a * a))
      }
    }
    if (currPosition.x > prevPosition.x && currPosition.y < prevPosition.y) {
      return {
        new_x: Math.sqrt((r * r) / (1 + a * a)),
        new_y: -Math.sqrt((a * a * r * r) / (1 + a * a))
      }
    } else if (currPosition.x > prevPosition.x && currPosition.y > prevPosition.y) {
      return {
        new_x: Math.sqrt((r * r) / (1 + a * a)),
        new_y: Math.sqrt((a * a * r * r) / (1 + a * a))
      }
    } else if (currPosition.x == prevPosition.x && currPosition.y < prevPosition.y) {
      return {
        new_x: 0,
        new_y: -r
      }
    } else if (currPosition.x == prevPosition.x && currPosition.y > prevPosition.y) {
      return {
        new_x: 0,
        new_y: r
      }
    } else if (currPosition.x < prevPosition.x && currPosition.y == prevPosition.y) {
      return {
        new_x: -r,
        new_y: 0
      }
    } else if (currPosition.x > prevPosition.x && currPosition.y == prevPosition.y) {
      return {
        new_x: r,
        new_y: 0
      }
    }


    return {
      new_x: 0,
      new_y: 0
    }

  }

  update1(x1, y1) {
    var new_x_y_1 = this.calculateCircleEdgePoint({
      x: x1,
      y: y1
    }, {
      x: this.state.x2,
      y: this.state.y2
    }, 30, 0);
    var new_x_y_2 = this.calculateCircleEdgePoint({
      x: x1,
      y: y1
    }, {
      x: this.state.x2,
      y: this.state.y2
    }, 30, this.state.r_diff);
    this.setState({
      x1: x1,
      y1: y1,
      x2: this.state.x2,
      y2: this.state.y2,
      fixed_x1: x1 - new_x_y_1.new_x,
      fixed_y1: y1 - new_x_y_1.new_y,
      fixed_x2: this.state.x2 + new_x_y_2.new_x,
      fixed_y2: this.state.y2 + new_x_y_2.new_y,
      label1: this.state.label1,
      label2: this.state.label2
    });
  }

  update2(x2, y2) {
    var new_x_y_1 = this.calculateCircleEdgePoint({
      x: this.state.x1,
      y: this.state.y1
    }, {
      x: x2,
      y: y2
    }, 30, 0);
    var new_x_y_2 = this.calculateCircleEdgePoint({
      x: this.state.x1,
      y: this.state.y1
    }, {
      x: x2,
      y: y2
    }, 30, this.state.r_diff);
    this.setState({
      x1: this.state.x1,
      y1: this.state.y1,
      x2: x2,
      y2: y2,
      fixed_x1: this.state.x1 - new_x_y_1.new_x,
      fixed_y1: this.state.y1 - new_x_y_1.new_y,
      fixed_x2: x2 + new_x_y_2.new_x,
      fixed_y2: y2 + new_x_y_2.new_y,
      label1: this.state.label1,
      label2: this.state.label2
    });
  }

  render() {
    return (
      <g>
        { this.state.isDirected? (
        <defs>
          <marker id={ this.trimForMarkerEnd(this.state.label1 + ":" + this.state.label2) } orient="auto" markerWidth='8' markerHeight='16' refX='0.1' refY='8'>
            <path d='M0,0 V16 L8,8 Z' />
          </marker>
        </defs>
        ):(<g></g>)}
        <line ref={ this.state.label1 + ":" + this.state.label2 } stroke="black" x1={ this.state.fixed_x1 } y1={ this.state.fixed_y1 } x2={ this.state.fixed_x2 } y2={ this.state.fixed_y2 }>
        </line>
      </g>
    )
  }
}
