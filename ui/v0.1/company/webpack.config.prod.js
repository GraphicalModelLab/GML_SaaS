var webpack = require('webpack');

module.exports = {
  entry: "./src/app/company/Index",
  output: {
    filename: "./dist/bundleCompanyProd.js"
  },

  resolve: {
    // Add '.ts' and '.tsx' as resolvable extensions.
    extensions: ["", ".js", ".jsx", ".css"]
  },

  plugins: [
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false
      }
    }),
    new webpack.DefinePlugin({
      'process.env':{
        'NODE_ENV': JSON.stringify('production')
      }
    })
  ],

  module: {
    loaders: [
                {
                  test: /\.jsx?$/,
                  exclude: /node_modules/,
                  loader: 'babel-loader',
                  query: {
                    presets: ['es2015'],
                    plugins: ['transform-react-jsx'],
                  },
                },
           {
                test: /\.css$/,
                loaders: ['style', 'css?modules'],
              }
        ]
  }
};;
