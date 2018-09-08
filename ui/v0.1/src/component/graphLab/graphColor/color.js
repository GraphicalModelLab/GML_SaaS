module.exports = {
  save(color) {
    localStorage.color = color
  },
  get(){
     return localStorage.color;
  }
}