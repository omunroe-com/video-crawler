module.exports = {
  site: {
    start: 'https://www.heavybit.com/library/',
    next: 'https://www.heavybit.com/library/page/{id}/',
    page: '/library/video/'
  },
  data: {
    speakerName_ss: () => () => [$(this, "meta[property='og:title']").text().split(" | ")[0]],
    title_s: () => () => $(this, "meta[property='og:title']").text().split(" | ")[1],
    description_s: () => () => $(this, 'og:description').text(),
    tag_s: () => () => [$(this, "meta[property='og:tag']").text()],
    url_s: () => () => $(this, "meta[property='og:url']").text(),
    captions_ss: () => $(this, '#transcript span').map( (v) => v.begin + ' ' + v.text()),
    talk_year_i: () => $(this, "meta[property='article:published_time']").text(),
    audio_length_f: () => {
        const all_times = $(this, '#transcript span');
        const time = all_times[all_times.length - 1]['begin']
        const parts = time.split(':');
        const last = parts[2].split(",");

        return 3600 * parseInt(parts[0]) + 60 * parseInt(parts[1]) +
               parseInt(last[0]) + parseInt(last[1]) / 1000.0;
    },
    video_url_s: () => $('video source[type="video/mp4"]').attr('src'),
    collection_ss: ['Heavybit']
  }
}
