import * as React from 'react';
import * as ReactDOM from 'react-dom';
import Draggable from 'react-draggable';
import NodePropertyView from './../graphProperty/nodePropertyView'


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

   deleteEdge(){
        this.props.deleteEdgeCallBack(this.props.label1, this.props.label2);
   }

   render() {
        return (
               <g onClick={this.deleteEdge}>
                    <circle
                        r="8"
                        cx={this.state.x}
                        cy={this.state.y}
                        fill="blue"
                    ></circle>

                    <line stroke="white" x1={this.state.x-3} y1={this.state.y-3} x2={this.state.x+3} y2={this.state.y+3} />

                    <line stroke="white" x1={this.state.x+3} y1={this.state.y-3} x2={this.state.x-3} y2={this.state.y+3} />
               </g>
           )
   }
}