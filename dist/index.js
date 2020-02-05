const fs = require('fs')
const http = require('http')

const dir = './logs'

http
  .createServer((_, res) => {
    res.setHeader('content-type', 'text/html')
    if (fs.existsSync(dir)) {
      const files = fs.readdirSync(dir)
      res.write('<ul>')
      res.write(`<li>---------------------------</li>`)

      files.forEach(file => {
        const log = JSON.parse(fs.readFileSync(`${dir}/${file}`))
        Object.entries(log).forEach(([key, val]) => {
          if (key === 'url') {
            res.write(`<li>${key}: <a href="${val}">${val}</a></li>`)
          } else {
            res.write(`<li>${key}: ${val}</li>`)
          }
        })

        res.write(`<li>---------------------------</li>`)
      })

      res.write('</ul>')
    }
    res.end()
  })
  .listen(3000, function() {
    console.log('Server started at localhost:3000')
  })
