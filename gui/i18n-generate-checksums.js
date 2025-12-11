const crypto = require('crypto');
const fs = require('fs');
const glob = require('glob');

function generateChecksum(str, algorithm, encoding) {
  return crypto
    .createHash(algorithm || 'md5')
    .update(str, 'utf8')
    .digest(encoding || 'hex');
}

const result = {};

glob.sync(`./src/assets/i18n/**/*.json`, { posix: true, dotRelative: true }).forEach(path => {
  const [_, lang] = path.split('src/assets/i18n/');
  const content = fs.readFileSync(path, {encoding: 'utf-8'});
  result[lang.replace('.json', '')] = generateChecksum(content);
});

fs.writeFileSync('./i18n-checksums.json', JSON.stringify(result));
