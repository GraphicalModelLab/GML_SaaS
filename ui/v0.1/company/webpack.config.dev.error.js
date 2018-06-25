module.exports = {
  entry: "./src/app/company/IndexError",
  output: {
    filename: "./dist/bundleCompanyError.js"
  },

  // Enable sourcemaps for debugging webpack's output.
  devtool: "source-map",

  resolve: {
    // Add '.ts' and '.tsx' as resolvable extensions.
    extensions: ["", ".js", ".jsx", ".css"]
  },

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
};
