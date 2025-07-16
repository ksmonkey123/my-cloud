package ch.awae.mycloud.ytdl.dto

import ch.awae.mycloud.ytdl.Job
import ch.awae.mycloud.ytdl.JobStatus
import ch.awae.mycloud.ytdl.OutputFormat

data class JobDto(
    val id: String,
    val url: String,
    val format: OutputFormat,
    val status: JobStatus,
    val files: List<FileDto>,
) {

    constructor(job: Job) : this(
        id = job.uuid.toString(),
        url = job.url,
        format = job.format,
        status = job.status,
        files = job.files.map { (name, size) -> FileDto(name, size) }.sortedBy { it.name }
    )

}

data class FileDto(
    val name: String,
    val size: Long,
)