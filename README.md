# Read
用于大三下Andorid课设设计的简单仿华为阅读App。
书源爬虫获取来自网页。
详细查看分支中的PPT
由于我是硬编码URL路径或页面结构，网站改版、路径变动都很常见，导致打不开相关。后面有时间会更新。
下面是替换暂时即可正常使用
BsTablefragment与shelf里用以下替换


public String getHtml(String imageUrl) {
        // 示例 imageUrl: http://www.xbiqugu.la/files/article/image/7/7004/7004s.jpg
        String[] parts = imageUrl.split("/");
        if (parts.length >= 7) {
            String id1 = parts[parts.length - 3]; // 7
            String id2 = parts[parts.length - 2]; // 7004
            return "http://www.xbiqugu.la/" + id1 + "/" + id2 + "/";
        }
        return "";
    }

Bookfetcher中用以下    //以下是改的新的后


private String getCover(String url) {
    try {
        // 获取最后一部分 bookId，例如 http://www.xbiqugu.net/xuanhuan/7004/ -> 7004
        String[] parts = url.split("/");
        String bookIdStr = parts[parts.length - 1]; // 注意最后是空的，可能需要 parts.length - 2
        if (bookIdStr == null || bookIdStr.isEmpty()) {
            bookIdStr = parts[parts.length - 2];
        }

        int bookId = Integer.parseInt(bookIdStr);
        int firstDir = bookId / 1000;

        return "http://www.xbiqugu.la/files/article/image/" + firstDir + "/" + bookId + "/" + bookId + "s.jpg";
    } catch (Exception e) {
        e.printStackTrace();
        return "https://yourdomain.com/default_cover.jpg"; // 替换为你自己的默认封面
    }
}
