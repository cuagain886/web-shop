import request from '@/utils/request'

/**
 * 获取已发布的公告列表
 */
export function getPublishedAnnouncements() {
  return request({
    url: '/api/announcements/published',
    method: 'get'
  })
}

/**
 * 分页查询公告
 */
export function pageAnnouncements(pageNum, pageSize, status) {
  return request({
    url: '/api/announcements/page',
    method: 'get',
    params: {
      pageNum,
      pageSize,
      status
    }
  })
}

/**
 * 获取公告详情
 */
export function getAnnouncement(id) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'get'
  })
}

/**
 * 创建公告
 */
export function createAnnouncement(data) {
  return request({
    url: '/api/announcements',
    method: 'post',
    data
  })
}

/**
 * 更新公告
 */
export function updateAnnouncement(id, data) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除公告
 */
export function deleteAnnouncement(id) {
  return request({
    url: `/api/announcements/${id}`,
    method: 'delete'
  })
}

/**
 * 发布公告
 */
export function publishAnnouncement(id) {
  return request({
    url: `/api/announcements/${id}/publish`,
    method: 'post'
  })
}
