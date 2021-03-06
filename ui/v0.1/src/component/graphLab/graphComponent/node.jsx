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
import NodePropertyView from './../graphProperty/nodePropertyView';
import color from "./../graphColor/color";
import NodeShape from "./../../constant/nodeShape";

export default class Node extends React.Component<Props, {}> {

  constructor(props) {
    super(props);

    this.state = {
      x: this.props.x,
      y: this.props.y,
      disable: this.props.disable,
      shape: this.props.shape,
      selected: false
    };

    this.handleMouseMove = this.handleMouseMove.bind(this);
    this.handleMouseDown = this.handleMouseDown.bind(this);
    this.handleMouseUp = this.handleMouseUp.bind(this);
    this.handleMouseEnterConnectedPoint = this.handleMouseEnterConnectedPoint.bind(this);
    this.clickNode = this.clickNode.bind(this);
    this.setDisable = this.setDisable.bind(this);
    this.getProperties = this.getProperties.bind(this);
    this.addProperties = this.addProperties.bind(this);
    this.resetMove = this.resetMove.bind(this);
    this.select = this.select.bind(this);
    this.deselect = this.deselect.bind(this);
    this.move = this.move.bind(this);
  }

  select(){
    this.setState({
      selected: true
    });
  }

  deselect(){
    this.setState({
      selected: false
    });
  }

  move(xDiff, yDiff){
    this.setState({
      x: this.state.x - xDiff * (1 / this.props.zoom),
      y: this.state.y - yDiff * (1 / this.props.zoom)
    });

    this.props.moveCircleCallBack(this.props.label, this.state.x, this.state.y);
  }

  handleMouseMove(e) {
    e.stopPropagation();
    const xDiff = this.coords.x - e.pageX;
    const yDiff = this.coords.y - e.pageY;

    this.coords.x = e.pageX;
    this.coords.y = e.pageY;

    this.setState({
      x: this.state.x - xDiff * (1 / this.props.zoom),
      y: this.state.y - yDiff * (1 / this.props.zoom)
    });

    this.props.moveCircleCallBack(this.props.label, this.state.x, this.state.y);

  /*console.log("mouse:"+this.state.x+","+this.state.y+","+e.pageX+","+e.pageY)

  if(Math.abs(this.state.x - e.pageX) > 30 || Math.abs(this.state.y - e.pageY)){
    console.log("off from the cursor");
    //this.resetMove();
  }
  */
  }

  componentDidMount() {
    if (this.props.properties) {
      this.addProperties(this.props.properties);
    }
  }

  handleMouseDown(e) {
    e.stopPropagation();
    this.coords = {
      x: e.pageX,
      y: e.pageY
    }

    this.mouseDownCoords = {
      x: e.pageX,
      y: e.pageY
    }

    document.addEventListener('mousemove', this.handleMouseMove);

  }

  handleMouseUp(e) {
    e.stopPropagation();

    // Check if this event is click
    if (e.pageX == this.mouseDownCoords.x && e.pageY == this.mouseDownCoords.y) {
      this.refs["nodePropertyView" + this.props.label].openModal();
    }

    this.resetMove();
  }

  resetMove() {
    document.removeEventListener('mousemove', this.handleMouseMove);
    this.coords = {};
  }

  handleMouseEnterConnectedPoint() {
    if (!this.state.disable) this.props.entryPointCallBack(this.props.label, this.state.x, this.state.y);
  }

  getCurrentPosition() {
    return {
      x: this.state.x,
      y: this.state.y
    }
  }

  clickNode() {
    if (this.state.disable) {
      this.setDisable(false);
    } else {
      this.props.deleteNodeCallBack(this.props.label);
    }
  }

  setDisable(disable) {
    this.setState({
      disable: disable
    });
  }

  getProperties() {
    return this.refs["nodePropertyView" + this.props.label].getProperties();
  }

  addProperties(properties) {
    return this.refs["nodePropertyView" + this.props.label].addProperties(properties);
  }

  render() {
    return (
      <g>
        <NodePropertyView modelparameter={ this.props.modelparameter } label={ this.props.label } properties={ this.props.properties } ref={ "nodePropertyView" + this.props.label } />

         { this.state.shape == NodeShape.CIRCLE ? (
            <g>
                <circle r={ 30 } cx={ this.state.x } cy={ this.state.y } onMouseDown={ this.handleMouseDown } onMouseUp={ this.handleMouseUp } fill={ (this.state.disable ? "#E0E0E0" : color.get()) }></circle>
                <text x={ this.state.x - 30 } y={ this.state.y + 40 } lengthAdjust="spacingAndGlyphs">
                  { this.props.label }
                </text>
            </g>
         ):(
            <g>
                <rect width={ 100 } height={ 100 } x={ this.state.x - 50  } y={ this.state.y - 50 } onMouseDown={ this.handleMouseDown } onMouseUp={ this.handleMouseUp } fill={ (this.state.disable ? "#E0E0E0" : color.get()) }></rect>
                <text x={ this.state.x - 50 } y={ this.state.y + 70 } lengthAdjust="spacingAndGlyphs">
                  { this.props.label }
                </text>
            </g>
         )}
        <g onMouseEnter={ this.handleMouseEnterConnectedPoint } onClick={ this.clickNode }>
          <circle r="10" cx={ this.state.x } cy={ this.state.y } fill={ (this.props.currentChosenNode == this.props.label ? "yellow" : "white") }></circle>
          <line stroke="black" x1={ this.state.disable ? this.state.x : this.state.x - 5 } y1={ this.state.y - 5 } x2={ this.state.disable ? this.state.x : this.state.x + 5 } y2={ this.state.y + 5 } />
          <line stroke="black" x1={ this.state.x + 5 } y1={ this.state.disable ? this.state.y : this.state.y - 5 } x2={ this.state.x - 5 } y2={ this.state.disable ? this.state.y : this.state.y + 5 } />
        </g>

        { this.state.selected ? (
                     <rect width={ 5 } height={ 5 } x={ this.state.x - 30  } y={ this.state.y - 30 }></rect>
                     )
                     :(<g></g>)
        }
      </g>
    )
  }
}
